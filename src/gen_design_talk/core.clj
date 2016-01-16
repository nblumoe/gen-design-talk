(ns gen-design-talk.core
  (:require [gen-design-talk.circles :as circles]
            [gen-design-talk.lines :as lines]
            [gen-design-talk.noise-2d :as noise-2d]
            [gen-design-talk.trees :as trees]
            [gen-design-talk.utils :as utils :refer [fullscreen-image-slide section]]
            [quil.core :as q]
            [quil.middleware :as m]))

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

   (section "What?" [160 50 100])

   (:simple lines/sketches)
   (:segmented lines/sketches)
   (:randomized lines/sketches)
   (:perlin lines/sketches)
   (:perlin-multi lines/sketches)
   (:parallax lines/sketches)

   (:circle circles/sketches)
   (:spiral circles/sketches)

   (:fuzzicle-evo-0 circles/sketches)
   (:fuzzicle-evo-1 circles/sketches)
   (:fuzzicle-evo-2 circles/sketches)
   (:fuzzicle circles/sketches)

   (:simple noise-2d/sketches)
   (:rotations noise-2d/sketches)

   (section "How?" [60 50 100])

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
  :size [500 500]
  :setup reset
  :settings (fn [] (q/smooth 8))
  :update update-state
  :draw draw
  :key-typed key-typed
  :key-pressed key-pressed
  :renderer :p2d
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode m/pause-on-error])
