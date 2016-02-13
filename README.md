# ![Alt text](/images/columns_logo.png "Columns, its what you read!")
Is a news reader app that lets users search the New York times article database and read articles. 

## Features
All required and optional user stories completed
- [x] User can enter a search query that will display a grid of news articles using the thumbnail and headline from the New York Times Search API.
- [x] User can click on "settings" which allows selection of advanced search options to filter results. 
- [x] User can configure advanced search filters such as:
  - [x] Begin Date
  - [x] News desk values 
  - [x] Sort order 
- [x] Subsequent searches will have any filters applied to the search results. 
- [x] User can tap on any article in results to view the contents in an embedded browser. 
- [x] User can scroll down "infinitely" to continue loading more news articles. The maximum number of articles is limited by the API search. 
- [x] Robust error handling, check if internet is available, handle error cases, network failures.
- [x] ActionBar SearchView as the query box
- [x] User can share a link to their friends or email it to themselves. 
- [x] Filter Settings displayed with a lightweight modal overlay.
- [x] RecyclerView with the StaggeredGridLayoutManager to display improve the grid of image results 
- [x] For different news articles that only have text or have text with thumbnails, use Heterogenous Layouts with RecyclerView. 
- [x] Apply the popular ButterKnife annotation library to reduce view boilerplate. 
- [x] Use Parcelable to pass Articles between activities
- [x] GSON library to streamline the parsing of JSON data. 
- [x] Glide for more efficient image rendering. 

## Extra
- [x] Scrolling animations: arcticles smoothly scale into view when scrolling.
- [x] Soft Toolbar: Toolbar is hidden when user scrolls down, and reappears when user scrolls back up
- [x] Splash Screen
- [x] Dynamic Toolbar menu items: When filters are set, solid filter icon, when they are empty, empty filter icon.
- [x] Category chooser null state:  When user has not yet entered a search, display an array of category options that auto populate filter.
- [x] Image loading placeholder:  Display default loading image while downloading image
- [x] Progress bar:  Display progress bar while loading news article

## Demo
### Basic user flow
![Alt text](/images/intro.gif)

### Applying Filters
![Alt text](/images/filters.gif)

### Sort By Time
![Alt text](/images/time_filter.gif)

### Progress bar
![Alt text](/images/progressbar.gif)


### Open Source Libraries Used
- [ButterKnife](http://jakewharton.github.io/butterknife/) Annotation library to reduce view boilerplate
- [Glide](https://github.com/bumptech/glide) Image downloading and caching library 
- [OKHttp](http://square.github.io/okhttp/) Networking
- [RecyclerView Animators](https://github.com/wasabeef/recyclerview-animators) RecyclerView Animiations
- [Parceler](https://github.com/johncarl81/parceler) Android Parcelables made easy through code generation
- [Hiding Scroll Listener](https://mzgreen.github.io/2015/02/28/How-to-hideshow-Toolbar-when-list-is-scrolling(part2)/) Hide Toolbar while scrolling

### Images
- [Icons8](https://icons8.com/android-icons/) Search, Filter icons

License
--------

    Copyright 2016 Mike Price.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
