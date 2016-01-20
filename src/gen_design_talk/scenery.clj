(ns gen-design-talk.scenery
  (:require [gen-design-talk.creatures :as creatures]
            [gen-design-talk.lines :as lines]
            [gen-design-talk.noise-2d :as noise]
            [gen-design-talk.creatures :as creatures]
            [gen-design-talk.utils :as utils]
            [quil.core :as q]
            ))

(defn parallax-landscape []
  (q/background 140 250 10)
  (q/no-stroke)

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
    (dorun (map (fn [n]
                  (q/fill 0 0 (+ (* n 10) 25))
                  (draw-bg (lines/perlin-line-segments (range x-min x-max 10)
                                                       (+ y-base (* (/  n 4) y-div))
                                                       (/ amplitude (* 0.15 (inc n)))
                                                       (+ (* n 100)
                                                          (* n n speed (/ time 1000))))))
                (concat (range 0 (inc num-backgrounds)) [5]))))
  )

(defn foggy-landscape [noise-seed]
  (parallax-landscape)
  (noise/fog 12 0.002 noise-seed 170))

(def sketches
  {:simple
   {:draw  (fn [state]
             (parallax-landscape)
             state)}

   :with-fog
   {:init (fn [state]
            (q/frame-rate 60)
            (q/no-stroke)
            (assoc state :noise-seed (rand 1000)))
    :update (fn [state]
              (update-in state [:noise-seed] + 0.01))
    :draw  (fn [{:keys [noise-seed] :as state}]
             (foggy-landscape noise-seed)
             state)}

   :with-creatures
   {:init (fn [state]
            (assoc state :noise-seed (rand 1000)
                   :creatures (repeatedly 10 (fn [] (assoc (creatures/create-creature)
                                                           :position [(rand (q/width))
                                                                      (+ (- (rand 100) 50) (* 0.8 (q/height)))])))))
    :update (fn [state]
              (-> state
                  (update-in [:noise-seed] + 0.01)
                  (update :creatures (fn [creatures]
                                       (map #(update % :position (fn [[x y]]
                                                                   (if (< x -10)
                                                                     [(+ (rand 20) (+ 10 (q/width)))
                                                                      (+ (- (rand 100) 50) (* 0.8 (q/height)))]
                                                                     [(- x 15) y]))) creatures)
                                       ))
                  ))
    :draw  (fn [{:keys [noise-seed creatures] :as state}]
             (parallax-landscape)
             (doseq [creature (sort-by #(second (:position %)) creatures)]
               (q/with-translation (:position creature)
                 (creatures/draw-creature creature)))
             (q/no-stroke)
             (noise/fog 12 0.002 noise-seed 170)
             state
             )}}
  )
