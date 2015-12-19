# FlickerSearch
That app is basically depends on keywords search, using Flicker'Apis.

Usage of App:
Users of your app should be able to fetch photos by searching for any particular keyword. 

Version Support:
The app support Android 4.0+.

The Api used to generate the app are :
1)flickr.photos.search --> To search for photos by entering search key value
2)flickr.photos.getInfo --> To get Information for each photo choosed by user
3)flickr.people.getPublicPhotos --> To get photos of specified user , by using owner id we get from the previous API

Libraries used to get the app functionality:
    1) com.android.support:appcompat-v7:23.0.1
    2) com.android.support:recyclerview-v7:23.0. --> to use RecyclerView 
    3) com.mcxiaoke.volley:library:1.0.19 --> to use Volley for serach functionality
    4) com.squareup.picasso:picasso:2.5.2 ---> To view Pictures on recyclerview
    5) com.android.support:cardview-v7:23.0.1 --? To use CardView inside recyclerview
    
    
