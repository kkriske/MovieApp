# MovieApp

Cross-platform [javafx 8](http://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm#BABEDDGH) application for managing and watching movies from your filesystem.

Movies will be streamed to vlc using [vlcj](http://capricasoftware.co.uk/#/projects/vlcj) ([github page](https://github.com/caprica/vlcj)) so you will require an up-to-date version of vlc

All information is fetched using [OMDb API](http://www.omdbapi.com/), the IMDb ID has to be set for each file individually.

This is still a work in progress.

### screenshots

#### settings
![settings](http://puu.sh/ja6tU/51a11b0381.png)

the specified directories are assumed to have the following layout:

* specified directory (D:/movies in the example above)
  * movie directory
    * movie file
    * optional srt files
  * movie direcotry
    * movie file
    * optional srt files
  * etc

#### main window
![main window](http://puu.sh/ja6vK/19e718acc3.png)

###### specifying IMDb ID
![thumbnail without IMDb ID](http://puu.sh/ja7jf/23e41b118f.png) ![thumbnail with IMDb ID](http://puu.sh/ja7oJ/b0c93b41cc.png)

#### info overview
![info overview](http://puu.sh/ja6x9/7b76158232.png)
