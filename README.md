# recyclerviewitemplaydemo

## Introduce
RecyclerView item 中播放视频的Demo
主要思路
	1. 在和RecyclerView的同级布局文件中，在右下角放置一个隐藏FrameLayout，当正在播放的列表滑出界面的时候，将右下角的FragmeLayout设置为显示，并将播放的SurfaceView添加到右下角的FragmeLayout播放。
	2. 在列表的ViewHolder布局文件中放置一个FragmeLayout，当点击播放按钮时，将SurfaceView添加到FragmentLayout中播放。
	3. 为RecyclerView设置addOnChildAttachStateChangeListener事件
	
	
## Screenshots

<img width="352" height=“625” src="https://github.com/wuxiaoqi123/recyclerviewitemplaydemo/blob/master/file/jdfw.gif"></img>

	
