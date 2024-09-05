(ns the-littlest-learner
  ;; - [ ] Make the plots from chapter one
  (:require [nextjournal.clerk.viewer :as clv]
            [nextjournal.clerk :as cle]
            [no.olavfosse.malt :refer :all])
  (:import java.awt.image.BufferedImage))
;; # Chapter 1

(def line-xs [2.0 1.0 4.0 3.0])
(def line-ys [1.8 1.2 4.2 3.3])
(plot {:range [[-1 5] [-1 5]]
       ;; :cells-per-axis-unit 2
       :points (map vector line-xs line-ys)})



(plot #(* 30 (Math/sin (/ % 10))))
(plot {:range [[-1 3] [-1 3]]
       :f #(Math/sin %)})

(plot {:range [[-1 3] [-1 3]]
       :f #(Math/sin %)})
(plot identity)
