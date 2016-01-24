(ns gen-design-talk.utils
  (:require [quil.core :as q]))

(defn screen-center []
  {:y (/ (q/height) 2)
   :x (/ (q/width) 2)})

(defn clear-screen [_]
  (q/background 20))

(defn transpose [m]
  (apply mapv vector m))

(defn negate-vector [[x y]]
  [(- x)
   (- y)])

(defn add-vectors [& vs]
  (mapv #(reduce + %) (transpose vs)))

(defn center-text [text]
  (q/text-size (/ (q/height) 10))
  (q/text-align :center :center)
  (q/text text (:x (screen-center)) (* 0.66 (:y (screen-center)))))

(defn image [filename]
  (q/background 0)
  (q/image-mode :center)
  (q/image (q/load-image filename) (:x (screen-center)) (:y (screen-center))))

(defn fullscreen-image-slide [filename]
  {:draw (fn [state]
           (image (str "resources/img/" filename)))})

(def triggered-rendering-handlers
  {:init (fn [state]
           (assoc state :re-render? true))
   :update (fn [{:keys [re-render?] :as state}]
             (assoc state
                    :render? re-render?
                    :re-render? false))})

(defn scheduled-rendering-handlers [intervall]
  {:init (fn [state]
           (assoc state :last-render -1))
   :update (fn [{:keys [last-render] :as state}]
             (let [current-slot (int (/ (q/millis) intervall))
                   re-render? (> current-slot last-render)]
               (assoc state
                      :render? re-render?
                      :last-render current-slot)))}
  )

(defn section
  ([title]
   (section title [0 0 0]))
  ([title color]
   {:title title
    :draw (fn [state]
            (apply q/background color)
            (q/fill (first color) 80 250)
            (center-text title))}))
