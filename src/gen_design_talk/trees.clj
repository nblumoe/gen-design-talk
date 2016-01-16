(ns gen-design-talk.trees
  (:require [gen-design-talk.utils :as utils]
            [quil.core :as q]))

(defn generate-segment [max-length max-angle]
  (let [rnd-length (rand (Math/floor max-length))
        rnd-angle (* (- (rand (* 2 max-angle))
                        (/ max-angle 2))
                     (/ Math/PI 180))
        x (Math/sin rnd-angle)
        y (Math/cos rnd-angle)
        magnitude (Math/sqrt (+ (* x x) (* y y)))
        x-norm (/ x magnitude)
        y-norm (/ y magnitude)]
    [(* rnd-length x)
     (* rnd-length y)]))

(defn generate-tree
  [{:as params :keys [segments branches thickness length
                      spread transform-branches]}]
  {:thickness thickness
   :segments (concat [[0 0]] (repeatedly segments (partial generate-segment (length params) (spread params))))
   :branches (if (> thickness 1)
               (repeatedly branches #(generate-tree (transform-branches params)))
               [])})

(def test-tree
  {:thickness 10
   :segments [[0 0]
              [10 10]
              [-20 30]
              [-30 0]
              ]
   :branches [{:segments [[0 0]
                          [10 10]
                          [40 10]]
               :thickness 9
               :branches []}
              {:segments [[0 0]
                          [-10 20]
                          [-20 15]]
               :thickness 9
               :branches [{:segments [[0 0]
                                      [1 10]
                                      [3 10]]
                           :thickness 5
                           :branches [{:segments [[0 0]
                                                  [1 10]
                                                  [3 10]]
                                       :thickness 2
                                       :branches []}
                                      {:segments [[0 0]
                                                  [-1 10]
                                                  [-3 10]]
                                       :thickness 3
                                       :branches []}
                                      ]}]}]})


(defn background []
  (q/background 200 50 100)
  (q/stroke-weight 1)
  (q/ellipse (:x (utils/screen-center)) (q/height) 200 200))

(def sketch
  {:title "Generative Design"
   :draw (fn [state]
           (let [draw-branch-segments (fn draw-branch-segments [[head & tail]]
                                        (when tail
                                          (q/with-translation (utils/negate-vector head)
                                            (q/line [0 0] (utils/negate-vector (first tail)))
                                            (draw-branch-segments tail))))
                 draw-tree (fn draw-tree [{:as tree :keys [branches segments thickness]}]
                             (q/stroke-weight thickness)
                             (draw-branch-segments segments)
                             (when (seq branches)
                               (q/with-translation (apply utils/add-vectors (map utils/negate-vector segments))
                                 (doseq [branch branches]
                                   (draw-tree branch)))))

                 gen-tree (partial generate-tree {:segments 6
                                                  :branches 2
                                                  :thickness 8
                                                  :length #(/ (+ (rand 100) (* 2 (:thickness %)))
                                                              (:segments %))
                                                  :spread #(/ 500 (q/pow 1.4 (:thickness %)))
                                                  :transform-branches #(-> %
                                                                           (update :branches (partial + 1))
                                                                           (update :thickness (partial + -2)))})
                 trees #(doseq [pos (range -10 11)]
                          (q/with-translation [(+ (:x (utils/screen-center))
                                                  (* pos (+ 70 (rand 30)))) (q/height)]
                            (draw-tree (gen-tree))))]

             (background)
             (utils/center-text "Generative Design")
             (q/stroke 100 0 30)
             (trees))
           state)})
