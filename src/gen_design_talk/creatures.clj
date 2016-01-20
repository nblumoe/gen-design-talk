(ns gen-design-talk.creatures
  (:require [gen-design-talk.utils :as utils]
            [quil.core :as q]))

(defn draw-eye [eye-size pupil-size]
  (q/ellipse 0 0 eye-size eye-size)
  (q/with-fill [10]
    (q/ellipse 0 0 pupil-size pupil-size)))

(defn body [width height]
  (q/ellipse 0 0 width height))

(defn head [body-height size]
  (q/ellipse 0 (* -0.5 body-height) size size))

(defn draw-creature [{:keys [body-width body-height
                             head-size
                             eyes-offset-x eyes-offset-y
                             eyes-size pupil-size
                             color]}]
  (q/with-translation [0 (/ body-height -2)]
    (q/stroke 1)

    (apply q/fill color)
    (body body-width body-height)
    (head body-height head-size)
    (q/fill 250)
    (dorun (map #(q/with-translation [(* % eyes-offset-x)
                                      (- (* -0.5 body-height) eyes-offset-y)]
                   (draw-eye eyes-size pupil-size)) [-1 1])))
  )

(defn rand-between [min max]
  (+ min (rand (- max min))))

(defn create-creature []
  {:body-height (rand-between 30 250)
   :body-width (rand-between 10 60)
   :head-size (rand-between 20 70)
   :eyes-size (rand-between 15 30)
   :pupil-size (rand-between 2 8)
   :eyes-offset-x (rand-between 10 15)
   :eyes-offset-y (rand-between 5 15)
   :color [(rand 250) (rand-between 100 250) (rand-between 150 250)]})

(def sketches
  {:simple {:init (fn [state]
                    (assoc state :creatures (repeatedly 10 create-creature)
                           :last-render 0))
            :update (fn [{:keys [last-render] :as state}]
                      (if (> (- (q/millis) last-render) 2000)
                        (assoc state :creatures (repeatedly 10 create-creature)
                               :last-render (q/millis))
                        state))
            :draw (fn [{:keys [creatures] :as state}]
                    (q/background 10)
                    (let [x-offset (/ (q/width) (inc (count creatures)))]
                      (q/with-translation [0 (* (q/height) 0.66)]
                        (doseq [creature creatures]
                          (q/translate x-offset 0)
                          (draw-creature creature))))
                    state
                    )}}
  )
