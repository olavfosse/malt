(ns notebook
  ;; - [ ] Make the plots from chapter one
  (:require [nextjournal.clerk.viewer :as clv]
            [nextjournal.clerk :as cle])
  (:import java.awt.image.BufferedImage))

(defn border? [height width x y]
  (or (= x (quot width 2)) (= y (quot height 2)) ))

(defn axis? [height width x y]
  (or (#{0 (dec width)} x) (#{0 (dec height)} y)))

(defn axes-grid [{:keys [width height unit]}]
  (let [img (BufferedImage. width height BufferedImage/TYPE_INT_RGB)]
    (dotimes [x width]
      (dotimes [y height]
        (.setRGB img x y
                 (cond
                   (axis? height width x y) 0x00
                   (border? height width x y) 0xFF
                   :else 0xF0F0F0 #_(+ 0xFFFFFF (* 10 x) (* 10 y))))))
    img))

(axes-grid {:height 300
            :width 600}
           #_{:grid/width 300
            :grid/height 300
            :grid/unit 20
            :axis/y-label
            :axis/x-label})

(defn set-rgb
  "Like .setRGB but tolerates out of bounds writes"
  [img x y rgb]
  (when (and (>= x 0) (< x (.getWidth img))
             (>= y 0) (< y (.getHeight img)))
    (.setRGB img x y rgb)))

(defn plot [x]
  (if (fn? x)
    (plot {:f x
           :width 600
           :height 300})
    (let [{:keys [f height width]} x
          grid (axes-grid x)
          origin-y (quot height 2)
          origin-x (quot width 2)]
      (doseq [x (range (- (quot width 2)) (quot width 2))]
        #_(set-rgb grid x (f x) 0xFF00)
        (set-rgb grid (+ origin-x x) (- origin-y (f x))  0xFF00))
      grid)))

(plot #(* 30 (Math/sin (/ % 10))))
(plot identity)

#_(graph {:width 2
        :span [10 10]})
#_(doto (BufferedImage. 300 300 BufferedImage/TYPE_INT_RGB)
  (.setRGB 0 0 0xffffff))

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
