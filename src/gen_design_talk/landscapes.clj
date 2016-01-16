(ns gen-design-talk.landscapes
  (:require [gen-design-talk.utils :as utils]
            [quil.core :as q]))

;; NOTES
;; Use 2d perlin and render landscape type based on height
;; maybe also reflect slope

(def sketches
  {:spiral
   (merge {:draw  (fn [{:keys [render?] :as state}]
                    (when render?
                      (q/stroke-weight 2)
                      (q/background 100 150 50)
                      (dorun (repeatedly 4 draw-spiral)))
                    state)}
          utils/triggered-rendering-handlers)
   })
