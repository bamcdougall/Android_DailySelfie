---
title       : Daily Selfie - Android Devices
subtitle    : Implementing Alarms & Threads
author      : B.A. McDougall
job         : NSCI Consulting
framework   : io2012        # {io2012, html5slides, shower, dzslides, ...}
highlighter : highlight.js  # {highlight.js, prettify, highlight}
hitheme     : tomorrow      # 
widgets     : []            # {mathjax, quiz, bootstrap}
mode        : selfcontained # {standalone, draft}
knit        : slidify::knit2slides
---

## Android Project - Daily Selfie

### App provides graphic interface for dedicated link to MOMA.org

### App uses "Overflow Menu" with Menu to "More Information"
* Provides user with options to open web browser that is linked to MOMA.org or cancel
* UI provides thumb slider that changes color of non-white / non-grey shapes
* Colors for transitions are randomly selected, but starting frame is recoverable
* Color transitions are controlled by ObjectAnimator.ofObject and started by the SeekBar

## Current Status of App
* [Screen-capture](http://youtu.be/C0nIZTxgETQ) shows video of this Android App

--- .class #id 
## Future Improvements
* Dynamically program shape sizes and shape placments based on screen size of host device

## Useful External Resource
* [Android Holo Colors Generator](http://android-holo-colors.com/) creates drawable resources consistent with color branding of the developer
