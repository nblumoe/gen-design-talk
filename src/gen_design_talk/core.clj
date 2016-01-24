(ns gen-design-talk.core
  (:require [gen-design-talk.circles :as circles]
            [gen-design-talk.grid :as grid]
            [gen-design-talk.lines :as lines]
            [gen-design-talk.noise-2d :as noise-2d]
            [gen-design-talk.scenery :as scenery]
            [gen-design-talk.trees :as trees]
            [gen-design-talk.utils :as utils :refer [fullscreen-image-slide section]]
            [quil.core :as q]
            [quil.middleware :as m]
            [gen-design-talk.creatures :as creatures]))

(defn sketches []
  [trees/sketch

   (section "Why?" [120 50 100])

   (fullscreen-image-slide "df2.jpg")
   (fullscreen-image-slide "generated_forest_landscape.jpg")

   (fullscreen-image-slide "ED_earth.jpg")
   (fullscreen-image-slide "ED_map_sol.jpg")
   (fullscreen-image-slide "ED_galaxy.jpg")

   (fullscreen-image-slide "lotr_cgi.jpg")
   (fullscreen-image-slide "avatar_cgi.jpg")
   (fullscreen-image-slide "3d-face-expression.jpg")

   (fullscreen-image-slide "data_visualization.JPG")
   (fullscreen-image-slide "cellular_logos.JPG")

   (fullscreen-image-slide "exhibition.JPG")
   (fullscreen-image-slide "installation.JPG")
   (fullscreen-image-slide "font_print.JPG")

   (section "What?" [160 50 100])

   (:simple lines/sketches)
   (:segmented lines/sketches)
   (:randomized lines/sketches)
   (:perlin lines/sketches)
   (:perlin-multi lines/sketches)

                                        ; quil-scratches/lines

   (:simple scenery/sketches)

   (:circle circles/sketches)
   (:spiral circles/sketches)

   (:fuzzicle-evo-0 circles/sketches)
   (:fuzzicle-evo-1 circles/sketches)
   (:fuzzicle-evo-2 circles/sketches)
   (:fuzzicle circles/sketches)

                                        ; quil-scratches/worms

   (:simple noise-2d/sketches)
   (:fog noise-2d/sketches)
   (:rotations noise-2d/sketches)
   (:with-fog scenery/sketches)

   (:simple creatures/sketches)
   (:with-creatures scenery/sketches)

                                        ; quil-sratches/circle
                                        ; quil-scratches/evo

   (:simple grid/sketches)
   (:more-directions grid/sketches)
   (:line-styles grid/sketches)
   (:line-styles2 grid/sketches)

   (section "How?" [60 50 100])
   (fullscreen-image-slide "processing.png")
   (fullscreen-image-slide "thing.png")
   (fullscreen-image-slide "node_box.png")

   (fullscreen-image-slide "generativedesign.jpg")
   (fullscreen-image-slide "generativeart.jpg")


   (section "Thank you" [100 0 0])
   ])

;; SKETCH NAVIGATION

(defonce current-sketch (atom 0))

(defn change-sketch [state mod-fn]
  (swap! current-sketch mod-fn)
  ((get-in (sketches) [@current-sketch :init] identity) state))

(defn key-typed [state {:keys [key]}]
  (case key
    :n (change-sketch state inc)
    (:p :b) (change-sketch state dec)
    :r (assoc state :re-render? true)
    state))

(defn key-pressed [state event]
  state)

;; ROOT SKETCH FUNCTIONS

(defn reset []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  (q/text-font (q/create-font "Droid Sans" 50 true))
  {})

(defn update-state [state]
  ((get-in (sketches) [@current-sketch :update] identity) state))

(defn draw-debug []
  (q/text-size 10)
  (q/text-align :left :top)
  (q/text (str "Sketch #" @current-sketch) 0 0))

(defn draw [state]
  ((get-in (sketches) [@current-sketch :draw] utils/clear-screen) state)
  (draw-debug))

;; ROOT SKETCH DECLARATION

(q/defsketch gen-design-talk
  :title "Generative Design Talk"
  :size [1024 768]
  :setup reset
  :settings (fn [] (q/smooth 8))
  :update update-state
  :draw draw
  :key-typed key-typed
  :key-pressed key-pressed
  :renderer :p2d
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode m/pause-on-error])
