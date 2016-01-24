(ns gen-design-talk.grid
  (:require [gen-design-talk.utils :as utils]
            [quil.core :as q]))

(defn draw-alternating-lines [grid-size drawing-fns]
  (q/background 0)
  (let [edge-length (q/height)
        tile-size (/ edge-length grid-size)]
    (doseq [i (range (/ (q/width) tile-size))
            j (range grid-size)]
      (let [x (* i tile-size)
            y (* j tile-size)]
        ((rand-nth drawing-fns) x y tile-size)))))

(defn draw-UR-to-LL
  [x y tile-size]
  (q/line (+ x tile-size) y x (+ y tile-size)))

(defn draw-UL-to-LR
  [x y tile-size]
  (q/line x y (+ x tile-size) (+ y tile-size)))

(defn draw-UR-to-LR
  [x y tile-size]
  (q/line (+ x tile-size) y (+ x tile-size) (+ y tile-size)))

(defn draw-UL-to-LL
  [x y tile-size]
  (q/line x y x (+ y tile-size)))

(defn draw-UL-to-UR
  [x y tile-size]
  (q/line x y (+ x tile-size) y))

(def sketches
  {:simple
   (merge {:draw (fn [{:keys [render?] :as state}]
                   (when render?
                     (q/stroke 200)
                     (q/stroke-weight 3)
                     (draw-alternating-lines 30
                                             [draw-UR-to-LL
                                              draw-UL-to-LR]))
                   state)}
          (utils/scheduled-rendering-handlers))

   :more-directions
   (merge {:draw (fn [{:keys [render?] :as state}]
                   (when render?
                     (q/stroke-weight 3)
                     (draw-alternating-lines 50
                                             [draw-UR-to-LL
                                              draw-UL-to-LR
                                              draw-UR-to-LR
                                              draw-UL-to-UR]))
                   state)}
          (utils/scheduled-rendering-handlers))

   :line-styles
   (merge {:draw (fn [{:keys [render?] :as state}]
                   (when render?
                     (draw-alternating-lines 40
                                             [(fn [& args]
                                                (q/stroke-weight 10)
                                                (q/stroke 100 200 100)
                                                (apply draw-UR-to-LL args))
                                              (fn [& args]
                                                (q/stroke-weight 8)
                                                (q/stroke 0 200 200)
                                                (apply draw-UL-to-LR args))
                                              (fn [& args]
                                                (q/stroke-weight 6)
                                                (q/stroke (rand 250) 200 100)
                                                (apply draw-UR-to-LR args))
                                              (fn [& args]
                                                (q/stroke-weight 4)
                                                (q/stroke (rand 250) 200 100)
                                                (apply draw-UL-to-UR args))
                                              ]))
                   state)}
          (utils/scheduled-rendering-handlers))

   :line-styles2
   (merge {:draw (fn [{:keys [render?] :as state}]
                   (when render?
                     (let [grid-size 20
                           tile-size (/ (q/height) grid-size)]
                       (q/stroke-cap :project)
                       (q/stroke-weight (/ tile-size 0.4))
                       (draw-alternating-lines grid-size
                                               [(fn [& args]
                                                  (q/stroke 100 250 250 100)
                                                  (apply draw-UR-to-LL args))
                                                (fn [& args]
                                                  (q/stroke 0 0 0 100)
                                                  (apply draw-UL-to-LR args))
                                                (fn [& args]
                                                  (q/stroke 00 200 200 100)
                                                  (apply draw-UL-to-UR args))
                                                (fn [& args]
                                                  (q/stroke 140 100 40 100)
                                                  (apply draw-UL-to-LL args))])
                       ))
                   state)}
          (utils/scheduled-rendering-handlers))

   })
