import cv2
from imutils.object_detection import non_max_suppression
import numpy as np
import imutils

# initialize the person detector
hog = cv2.HOGDescriptor()
hog.setSVMDetector(cv2.HOGDescriptor_getDefaultPeopleDetector())

cap = cv2.VideoCapture('TownCentreXVID.avi')

while (cap.isOpened()):
    ret, frame = cap.read()
    frame = imutils.resize(frame, width=min(1000, frame.shape[1]))

    (rects, weights) = hog.detectMultiScale(frame, winStride=(5, 5), padding=(8, 8), scale=1.25)
    for (x, y, w, h) in rects:
        cv2.rectangle(frame, (x, y), (x+w, y+h), (0,0,225), 2)

    cv2.imshow("test", frame)
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
