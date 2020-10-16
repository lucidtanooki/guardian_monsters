# How to use the LibGDX UI System from Scene2d

## ProgressBar

To style a progressbar a 9-patch file is needed in the
assets. This is integrated into the stylesheet via
TexturePacker. The 9-patch-tool can be found in the
Android SDK tools folder.

Then simply add the following to the JSON-File:

com.badlogic.gdx.scenes.scene2d.ui.ProgressBar$ProgressBarStyle: {
	  hp: { background: invis, knobBefore: blue-bar-lr  }
}

Style name: hp
background: empty image
progress img: a 9-patch-image named blue-bar-lr
