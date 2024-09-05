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
(defn- border? [{:as spec :keys [width height]} x y]
  (or (#{0 (dec width)} x) (#{0 (dec height)} y)))

(defn- axis? [{:as spec :keys [width height]} x y]
  (let [[x-offset y-offset] (origin-offset spec)]
    (or (= x x-offset) (= y y-offset))))

(defn axes-grid [{:as spec :keys [width height unit]}]
  (let [img (BufferedImage. width height BufferedImage/TYPE_INT_RGB)]
    (dotimes [x width]
      (dotimes [y height]
        (.setRGB img x y
                 (cond
                   (axis? spec x y) 0xFF
                   (border? spec x y) 0x00
                   :else 0xF0F0F0 #_(+ 0xFFFFFF (* 10 x) (* 10 y))))))
    img))

(defn set-rgb
  "Like .setRGB but tolerates out of bounds writes"
  [img x y rgb]
  (when (and (>= x 0) (< x (.getWidth img))
             (>= y 0) (< y (.getHeight img)))
    (.setRGB img x y rgb)))

(defn project [{:as spec
                :keys [width height]
                [x-range y-range] :range}]
  (let [[x-unit y-unit] (unit-lengths spec)
        [x-offset y-offset] (origin-offset spec)]
    (fn [[x y]]
      [(+ x-offset (* x x-unit)) (- y-offset (* y y-unit))])))

(defn fill-out [spec]
  (merge {:range [[-3 3] [-3 3]]
          :width 300
          :height 300}
         spec))

;; Glossary
;; ---------
;; Offset   The coordinate of a pixel in a raster

(defn unit-lengths [{:as spec
                     :keys [width height]
                     [[x-min x-max] [y-min y-max]] :range}]
  (let [x-units (- x-max x-min)
        y-units (- y-max y-min)]
    [(/ width x-units) (/ height y-units)]))

(defn origin-offset [{:as spec
                      :keys [height]
                      [[x-min x-max] [y-min y-max]] :range}]
  (let [[x-unit y-unit] (unit-lengths spec)]
    [(* x-unit (- x-min)) (+ height (* y-unit y-min))]))

(defn draw-dot [img x y]
  (set-rgb img x (dec y) 0)
  (set-rgb img (dec x) y 0)
  (set-rgb img x y 0)
  (set-rgb img (inc x) y 0)
  (set-rgb img x (inc y) 0))

(defn plot-points [{:as spec :keys [range points]}]
  ;; WT=wishful thought
  ;; WT: axes-grid understand range
  (let [spec (fill-out spec)
        grid (axes-grid spec)]
    (transduce (map (project spec))
               (completing (fn [_ [x y]] (draw-dot grid x y)))
               nil
               points)
    grid))

(defn plot-fn [{:as spec
                :keys [f height width]
                [[x-min x-max]] :range}]
  (let [grid (axes-grid spec)]
    (doseq [[x y] (->> (range x-min x-max 0.001)
                       (map (juxt identity f))
                       (map (project spec)))]
      (set-rgb grid x y  0xFF00))
    grid))

(defn plot [spec]
  (if (fn? spec)
    (plot {:f spec})
    (let [spec (fill-out spec)]
      (if (:points spec)
        (plot-points spec)
        (plot-fn spec)))))

