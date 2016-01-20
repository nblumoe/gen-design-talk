(ns gen-design-talk.noise-2d
  (:require [gen-design-talk.utils :as utils]
            [quil.core :as q]))

(defn fog
  ([step-size  granularity noise-seed]
   (fog step-size granularity noise-seed 255))
  ([step-size  granularity noise-seed max-alpha]
   (doseq [x (range 0 (q/width) step-size)
           y (range 0 (q/height) step-size)
           n (range 1 3)]
     (let [alpha (* max-alpha (q/noise (+ (* n n noise-seed) (* x granularity))
                                       (+ (* n noise-seed) (* y granularity))))]
       (q/fill 0 0 90 alpha)
       (q/rect x y step-size step-size))))

  )

(def sketches
  {:simple
   (merge {:draw (fn [{:keys [render?]}]
                   (when render?
                     (let [step-size 3
                           noise-seed (rand 1000)
                           granularity 0.02]
                       (q/background 0)
                       (q/no-stroke)
                       (doseq [x (range 0 (q/width) step-size)
                               y (range 0 (q/height) step-size)]
                         (let [alpha (* 255 (q/noise (+ noise-seed (* x granularity))
                                                     (+ noise-seed (* y granularity))))]
                           (q/fill 0 0 255 alpha)
                           (q/rect x y step-size step-size)))))
                   )}
          utils/triggered-rendering-handlers)

   :fog
   {:init (fn [state]
            (q/frame-rate 60)
            (q/no-stroke)
            (assoc state :noise-seed (rand 1000)))
    :update (fn [state]
              (update-in state [:noise-seed] + 0.01))
    :draw (fn [{:keys [noise-seed]}]
            (let [step-size 10
                  granularity 0.003]
              (q/background 0)
              (fog step-size granularity noise-seed)))}

   :rotations
   {:init (fn [state]
            (q/frame-rate 30)
            (assoc state :noise-seed (rand 1000)))
    :update (fn [state]
              (update-in state [:noise-seed] + 0.01))
    :draw (fn [{:keys [render? noise-seed]}]
            (let [step-size 7
                  granularity 0.01]
              (q/background 0)
              (q/stroke 90 160 190)
              (q/stroke-weight 1)
              (doseq [x (range 0 (/ (q/width) 2) step-size)
                      y (range 0 (/ (q/height) 2) step-size)]
                (let [noise (q/noise (+ noise-seed (* x granularity))
                                     (+ noise-seed (* y granularity)))]
                  (q/with-translation [(+ x (/ (q/width) 4)) (+ y (/ (q/height) 4))]
                    (q/with-rotation [(* noise 2 Math/PI )]
                      (q/stroke (* noise 255) 160 255 (* noise 255))
                      (q/line 0 0 40 0))))))
            )}


   }
  )
