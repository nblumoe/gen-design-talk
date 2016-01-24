(ns gen-design-talk.grid
  (:require [gen-design-talk.utils :as utils]
            [quil.core :as q]))

(defn draw-alternating-lines [grid-size drawing-fns]
  (q/background 0)
  (let [edge-length (q/height)
        grid-size grid-size
        tile-size (/ edge-length grid-size)]
    (q/stroke 250)
    (q/stroke-weight 1)
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

(defn reset []
  (q/frame-rate 1))

(def sketches
  {:simple
   {:init (fn [state]
            (reset)
            {})

    :draw (fn [state]
            (draw-alternating-lines 30
                                    [draw-UR-to-LL
                                     draw-UL-to-LR])
            state)}

   :more-directions
   {:init (fn [state]
            (reset)
            {})
    :draw (fn [state]
            (draw-alternating-lines 75
                                    [draw-UR-to-LL
                                     draw-UL-to-LR
                                     draw-UR-to-LR
                                     draw-UL-to-UR])
            state)}

   :line-styles
   {:init (fn [state]
            (reset)
            {})
    :draw (fn [state]
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
                                     ])
            state)}

   })
