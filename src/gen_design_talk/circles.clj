(ns gen-design-talk.circles
  (:require [gen-design-talk.utils :as utils]
            [quil.core :as q]))

(defn point-on-circle
  ([angle]
   (point-on-circle angle 1))
  ([angle radius]
   [(* radius (q/cos angle))
    (* radius (q/sin angle))]))

(defn points-on-circle [start diff]
  (lazy-seq (cons (point-on-circle start)
                  (points-on-circle (+ start diff) diff))))


(defn- draw-spiral []
  (let [num-segments 100
        max-angle (* 10 2 Math/PI)
        diff-angle (/ Math/PI num-segments 0.5)
        diff-radius 0
        noise-amplitude 80
        center-x (/ (q/width) 2)
        center-y (/ (q/height) 2)]
    (loop [angle 0 radius 0 noise (rand 100)]
      (let [new-angle (+ diff-angle angle)
            new-radius (+ (+ diff-radius radius)
                          (- (* (q/noise noise) noise-amplitude)
                             (/ noise-amplitude 2)))
            x1 (+ center-x (* radius (Math/cos angle)))
            y1 (+ center-y (* radius (Math/sin angle)))
            x2 (+ center-x (* new-radius (Math/cos new-angle)))
            y2 (+ center-y (* new-radius (Math/sin new-angle)))]
        (q/stroke 80 0 200 (* angle 1.3))
        (q/stroke-weight (* angle 0.2))
        (q/line x1 y1 x2 y2)
        (when (< new-angle max-angle)
          (recur new-angle new-radius (+ noise 0.05)))))))

(defn draw-fuzzicle [& {:keys [position radius noise-seed
                               starting-angle max-angle angle-step-size
                               hue color-fn
                               angle-randomizer position-randomizer radius-randomizer]
                        :or {position [(/ (q/width) 2) (/ (q/height) 2)]
                             noise-seed (q/random 100)
                             radius 200
                             angle-step-size 0.003
                             starting-angle (rand Math/PI)
                             hue (rand 255)
                             color-fn (fn [hue angle-ratio]
                                        [hue (* 255 angle-ratio) (* 255 angle-ratio)])
                             max-angle (* 4 Math/PI)
                             angle-randomizer (fn [angle] (q/noise (+ (* 2 angle) noise-seed)))
                             position-randomizer (fn [angle position]
                                                   (mapv #(+ % (* 100 (q/noise (+ (* 2 angle) noise-seed))) -50) position))
                             radius-randomizer (fn [angle angle-ratio radius]
                                                 (- (* (q/noise (+ (* 2 angle) noise-seed))
                                                       radius)
                                                    (* 200 angle-ratio)))
                             }}]
  (doseq [angle (range 0 max-angle angle-step-size)]
    (let [angle-ratio (/ angle max-angle)
          rand-angle (+ angle
                        starting-angle
                        (angle-randomizer angle))
          rand-position (position-randomizer angle position)
          rand-radius (radius-randomizer angle angle-ratio radius)]
      (apply q/stroke (color-fn hue angle-ratio))
      (q/with-translation rand-position
        (q/line (point-on-circle rand-angle rand-radius)
                (point-on-circle (+ rand-angle Math/PI) rand-radius))))))


(defn draw-circle-segmented [pos radius size num-points]
  (q/with-translation pos
    (q/with-fill [0 0 0 0]
      (q/with-stroke [0 0 255]
        (q/stroke-weight 3)
        (q/ellipse 0 0 (* 2 radius) (* 2 radius))))
    (doseq [[x y] (take num-points
                        (points-on-circle (* (q/millis) 0.0009)
                                          (/ Math/PI num-points 0.5)))]
      (let [;radius (+ radius -50 (* 100 (q/noise (rand 5))))
            xr (* x radius)
            yr (* y radius)]
        (q/ellipse xr yr size size)))))

(def sketches
  {:spiral
   (merge {:draw  (fn [{:keys [render?] :as state}]
                    (when render?
                      (q/stroke-weight 0.1)
                      (q/background 100 110 30)
                      (dorun (repeatedly 1 draw-spiral)))
                    state)}
          utils/triggered-rendering-handlers)

   :circle
   (merge {:draw (fn [{:keys [render?] :as state}]
                   (when render?
                     (q/no-stroke)
                     (q/background 0)
                     (q/fill 120 0 200)
                     (let [{:keys [x y]} (utils/screen-center)]
                       (draw-circle-segmented [x y] 200 20 30)))
                   )}
          utils/triggered-rendering-handlers)

   :fuzzicle-evo-0
   {:draw (fn [state]
            (q/background 0)
            (q/stroke-weight 0.5)
            (q/stroke 200)

            (draw-fuzzicle :angle-randomizer (constantly 0)
                           :radius-randomizer (fn [_ _ radius] radius)
                           :position-randomizer (fn [_ position] position)
                           :max-angle (* 1 Math/PI)
                           :starting-angle 0
                           :color-fn (fn [_ _] [0 0 255])
                           :angle-step-size 0.05))}

   :fuzzicle-evo-1
   (merge {:draw (fn [{:keys [render?]}]
                   (when render?
                     (q/background 0)
                     (q/stroke-weight 0.6)
                     (q/stroke 200)

                     (draw-fuzzicle :angle-randomizer (constantly 0)
                                    :radius 800
                                    :position-randomizer (fn [_ position] position)
                                    :max-angle Math/PI
                                    :starting-angle 0
                                    :color-fn (fn [_ _] [0 0 255])
                                    :angle-step-size 0.05)))}
          utils/triggered-rendering-handlers)


   :fuzzicle-evo-2
   (merge {:draw (fn [{:keys [render?]}]
                   (when render?
                     (q/background 0)
                     (q/stroke-weight 0.6)
                     (q/stroke 200)

                     (draw-fuzzicle :radius 800
                                    :position-randomizer (fn [_ position] position)
                                    :max-angle Math/PI
                                    :starting-angle 0
                                    :color-fn (fn [_ _] [0 0 255])
                                    :angle-step-size 0.01)))}
          utils/triggered-rendering-handlers)


   :fuzzicle
   (merge {:draw (fn [{:keys [radius noise-seed render?]}]
                   (when render?
                     (q/background 0)
                     (q/stroke-weight 0.5)
                     (q/stroke 200)
                     (draw-fuzzicle :radius 700)))}
          utils/triggered-rendering-handlers)

   })
