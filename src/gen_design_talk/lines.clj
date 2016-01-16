(ns gen-design-talk.lines
  (:require [gen-design-talk.utils :as utils]
            [quil.core :as q]))


(defn perlin-line-segments [xs y-base amplitude rand-seed]
  (partition 2 (interleave xs
                           (map #(+ (* amplitude (q/noise (+ rand-seed (/ % 100))))
                                    (- (/ amplitude 2))
                                    y-base) xs))))

(defn segmented-line [segments]
  (loop [[head & tail] segments]
    (when tail
      (q/line head (first tail))
      (recur tail))))

(def sketches
  (let [reset (fn []
                (q/background 100 50 50)
                (q/stroke-weight 20)
                (q/stroke 100 200 200))
        margin 100
        x1 margin
        x2 #(- (q/width) margin)
        y #(:y (utils/screen-center))
        dx 20]

    {:simple
     {:draw (fn [state]
              (reset)
              (q/line x1 (y) (x2) (y))
              state)}

     :segmented
     {:draw (fn [state]
              (let [xs (range x1 (x2) dx)
                    ys (repeat (y))
                    line-segments (concat  (partition 2 (interleave xs ys)) [[(x2) (y)]])]
                (reset)
                (q/stroke 100 200 200 100)
                (segmented-line line-segments))
              state)}

     :randomized
     (merge {:draw (fn [{:keys [render?] :as state}]
                     (when render?
                       (let [xs (range x1 (x2) dx)
                             ys (repeat (y))
                             line-segments (partition 2 (interleave xs (map #(+ (q/random -200 200) %) ys)))]
                         (reset)
                         (segmented-line line-segments)))
                     state)}
            utils/triggered-rendering-handlers)

     :perlin
     (merge {:draw (fn [{:keys [render?] :as state}]
                     (when render?
                       (let [xs (range x1 (x2) dx)
                             rand-seed (rand 1000)
                             amplitude 400
                             line-segments (perlin-line-segments xs (y) amplitude (rand 100))]
                         (reset)
                         (segmented-line line-segments)))
                     state)}
            utils/triggered-rendering-handlers)

     :perlin-multi
     (merge  {:draw (fn [{:keys [render?] :as state}]
                      (when render?
                        (reset)
                        (dotimes [n 10]
                          (q/stroke-weight (- 15 n))
                          (q/stroke 100 (- 200 (* 40 n)) 200)
                          (let [xs (range x1 (x2) dx)
                                rand-seed (rand 1000)
                                amplitude 200
                                line-segments (partition 2 (interleave xs
                                                                       (map #(+ (* -1 (/ amplitude 2))
                                                                                (* amplitude (q/noise (+ rand-seed (/ % 40))))
                                                                                (- (+ (* n 75) (y)) 300)) xs)))]
                            (segmented-line line-segments))))
                      state)}
             utils/triggered-rendering-handlers)

     :parallax
     {:draw  (fn [{:keys [render?] :as state}]
               (let [num-backgrounds 2
                     x-min -20
                     x-max (+ 20 (q/width))
                     y-base (/ (q/height) 3)
                     y-div 200
                     speed 0.08
                     y-max (+ 20 (q/height))
                     amplitude 30
                     draw-bg (fn [segments]
                               (q/begin-shape)
                               (doseq [[x y] segments]
                                 (q/vertex x y))
                               (q/vertex x-max y-max)
                               (q/vertex x-min y-max)
                               (q/vertex x-min y-base)
                               (q/end-shape))
                     time (q/millis)]
                 (q/background 140 250 10)
                 (q/no-stroke)
                 (dorun (map (fn [n]
                               (q/fill 0 0 (+ (* n 10) 25))
                               (draw-bg (perlin-line-segments (range x-min x-max 10)
                                                              (+ y-base (* (/  n 4) y-div))
                                                              (/ amplitude (* 0.15 (inc n)))
                                                              (+ (* n 100)
                                                                 (* n n speed (/ time 1000))))))
                             (concat (range 0 (inc num-backgrounds)) [5]))))
               state)}
     }))
