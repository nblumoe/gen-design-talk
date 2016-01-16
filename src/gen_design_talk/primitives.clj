(ns gen-design-talk.lines
  (:require [gen-design-talk.utils :as utils]
            [quil.core :as q]))

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

    {:simple-line
     {:draw (fn [state]
              (reset)
              (q/line x1 (y) (x2) (y))
              state)}

     :segmented-line
     {:draw (fn [state]
              (let [xs (range x1 (x2) dx)
                    ys (repeat (y))
                    line-segments (concat  (partition 2 (interleave xs ys)) [[(x2) (y)]])]
                (reset)
                (q/stroke 100 200 200 100)
                (segmented-line line-segments))
              state)}

     :randomized-line
     (merge {:draw (fn [{:keys [render?] :as state}]
                     (when render?
                       (let [xs (range x1 (x2) dx)
                             ys (repeat (y))
                             line-segments (partition 2 (interleave xs (map #(+ (q/random -200 200) %) ys)))]
                         (reset)
                         (segmented-line line-segments)))
                     state)}
            utils/triggered-rendering-handlers)

     :perlin-line
     (merge {:draw (fn [{:keys [render?] :as state}]
                     (when render?
                       (let [xs (range x1 (x2) dx)
                             rand-seed (rand 1000)
                             line-segments (partition 2 (interleave xs
                                                                    (map #(+ -200 (* 400 (q/noise (+ rand-seed (/ % 100)))) (y)) xs)))]
                         (reset)
                         (segmented-line line-segments)))
                     state)}
            utils/triggered-rendering-handlers)

     :perlin-lines
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

     }))
