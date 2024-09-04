(ns the-littlest-learner
  ;; - [ ] Make the plots from chapter one
  (:require [nextjournal.clerk.viewer :as clv]
            [nextjournal.clerk :as cle]
            [no.olavfosse.malt :refer-all])
  (:import java.awt.image.BufferedImage))

(axes-grid {:height 300
            :width 600})
(plot #(* 30 (Math/sin (/ % 10))))
(plot identity)

(def line-xs [2.0 1.0 4.0 3.0])
(def line-ys [1.8 1.2 4.2 3.3])

clv/default-viewers

(clv/present 1)
(clv/present #{1 2 3})

(clv/with-viewer {:transform-fn clv/inspect-wrapped-values}
  "Exploring the viewer api")
(def greet-viewer
  {:transform-fn (cle/update-val #(cle/html [:strong "Hello, " % " 👋"]))})
;; Present -JVM/JS-> Transform

(clv/with-viewer greet-viewer
  "Olav")


;; Very nice, :render-fn runs on client side.
;;
;; To viz a fn I could either sample the fn on jvm and then transfer
;; or i'd need to pass the fn symbolically
(clv/with-viewer {:render-fn '(fn [idek] [:div
                                          [:p "hey"]
                                          [:p "ho"]])}
  "Olav")

;; ATTENTION: I've been thinking about this the wrong way; For now I
;;            just need to render a java.awt.image.BufferedImage JVM
;;            side and sent it OTW. Interactive viz is way down the
;;            line. https://book.clerk.vision/#images
;;
;; With JVM rendered graph and numerical nabla, I could get pretty far
;; if I put 2 hours into this project a day.
