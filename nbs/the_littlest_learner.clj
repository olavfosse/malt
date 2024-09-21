(ns the-littlest-learner
  ;; - [ ] Make the plots from chapter one
  (:require [nextjournal.clerk.viewer :as clv]
            [nextjournal.clerk :as cle]
            [no.olavfosse.malt :refer :all])
  (:import java.awt.image.BufferedImage))





(def line-xs [2.0 1.0 4.0 3.0])
(def line-ys [1.8 1.2 4.2 3.3])
#_(time (dotimes [n 100] (plot {:features [(map vector line-xs line-ys)
                                    (partial + 0.1)]})))
(plot {:range [[-1 5] [-1 5]]
       ;; :cells-per-axis-unit 2
       :points (map vector line-xs line-ys)})

(plot #(* 30 (Math/sin (/ % 10))))
(plot {:range [[-1 3] [-1 3]]
       :f #(Math/sin %)})

(plot {:range [[-1 3] [-1 3]]
       :f #(Math/sin %)})
(plot identity)

(plot {:features [identity
                  (map vector line-xs line-ys)]})


;;

(def sum¹ identity)

(sum¹ 123)


;;
((l2-loss line) line-xs line-ys)

(def quad-xs [-1.0 0.0 1.0 2.0 3.0])
(def quad-ys [2.55 2.1 4.35 10.2 18.25])

;; # Glossary
;; ## θ
;; A set of parameters
;; ## θ'
;; An accompanied set of parameters - a set of parameters accompanied by some meta data. 
;; ## Loss function
;; A fn taking a θ and returning an expectant function. Example:
l2-loss
;; ## Expectant function
;; A fn taking a dataset and returning an objective function. Example:
(l2-loss line)
;; ## Objective function
;; A fn taking a θ and computing a loss value. 
((l2-loss line) line-xs line-ys)
;; ## Loss value
;; A real number measuring how well we're achieving our objective. The lower the number the better.
'(((l2-loss line) line-xs line-ys) [0 1])

