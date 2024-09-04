(ns no.olavfosse.malt
  ;; - [ ] Make the plots from chapter one
  (:require [nextjournal.clerk.viewer :as clv]
            [nextjournal.clerk :as cle])
  (:import java.awt.image.BufferedImage))

;; Perhaps I should think in terms of projection?

;; ===========================
;;       Visualization &
;;         Tangibility
;; ===========================
(defn- border? [height width x y]
  (or (= x (quot width 2)) (= y (quot height 2)) ))

(defn- axis? [height width x y]
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

(defn set-rgb
  "Like .setRGB but tolerates out of bounds writes"
  [img x y rgb]
  (when (and (>= x 0) (< x (.getWidth img))
             (>= y 0) (< y (.getHeight img)))
    (.setRGB img x y rgb)))

(defn plot-points [{:as spec :keys [range points]}]
  ;; WT=wishful thought
  ;; WT: axes-grid understand range
  (let [grid (axes-grid spec)]
    (doseq [[x y] (map (project spec) points)]
      (draw-dot img x y))
    grid))
run!
(defn plot [x]
  (cond
    (fn? x) (plot {:f x
                   :width 600
                   :height 300})
    (:points x) (plot-points x)
    :else (let [{:keys [f height width]} x
                grid (axes-grid x)
                origin-y (quot height 2)
                origin-x (quot width 2)]
            (doseq [x (range (- (quot width 2)) (quot width 2))]
              #_(set-rgb grid x (f x) 0xFF00)
              (set-rgb grid (+ origin-x x) (- origin-y (f x))  0xFF00))
            grid)))

