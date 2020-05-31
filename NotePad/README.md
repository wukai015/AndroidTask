# 基于NotePad应用做功能扩展（期中作业）
阅读NotePad的源代码并做如下扩展：

* NoteList中显示条目增加时间戳显示

* 添加笔记查询功能（根据标题查询）

## 原界面

![image](http://chuantu.xyz/t6/737/1590915819x989499252.png)

## 一、增加时间戳显示

### 1.更改布局文件noteslist_item.xml，添加一个TextView显示时间戳
```XML
<LinearLayout  xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    >
    <TextView xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@android:id/text1"
        android:layout_width="match_parent"
        android:layout_height="40dp"
        android:gravity="center_vertical"
        android:singleLine="true"
        android:textAppearance="?android:attr/textAppearanceLarge"
        android:textSize="30dp"
        android:text="title"
        />

    <TextView
        android:id="@+id/text2"
        android:layout_width="match_parent"
        android:layout_height="25dp"
        android:textAlignment="center"
        android:textSize="18dp"
        android:gravity="center_vertical"
        android:text="最近修改时间"
        />

</LinearLayout>
```

![image](http://chuantu.xyz/t6/737/1590916710x989499252.png)

### 2.数据库原先已存在时间戳字段，只需要修改NotesList类的例表投影，把时间显示出来

（1）分析SimpleCursorAdapter类的实例对象
```java
 SimpleCursorAdapter adapter
            = new SimpleCursorAdapter(
                      this,                             // The Context for the ListView
                      R.layout.noteslist_item,          // Points to the XML for a list item
                      cursor,                           // The cursor to get items from
                      dataColumns,
                      viewIDs
              );
```

(2)其中构造参数cursor是查询数据库返回的cursor对象
```java
Cursor cursor = managedQuery(
            getIntent().getData(),            // Use the default content URI for the provider.
            PROJECTION,                       // Return the note ID and title for each note.
            null,                             // No where clause, return all records.
            null,                             // No where clause, therefore no where column values.
            NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
```

- cursor中的PROJECTION数组包含要查询的列名，我们需要投影时间，所以添加列名“修改时间”
```java
    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE,//修改时间
    };
```

(3)SimpleCursorAdapter类的构造参数dataColumns数组包含要投影的列名，同样添加列名“修改时间”
```java
 // The names of the cursor columns to display in the view, initialized to the title column
        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE } ;
```

(4）构造参数viewIDs包含数据对应的组件ID，为时间戳对应组件text2
```java
// noteslist_item.xml
        int[] viewIDs = { android.R.id.text1,R.id.text2};
```
(5)修改完后时间已经可以投影出来，但投影出来的是毫秒，需要转换为时间格式的字符串

![image](http://chuantu.xyz/t6/737/1590918836x989499252.png)

### 3.使用SimpleCursorAdapter.ViewBinder可以将SimpleCursorAdapter的数据显示成转化后的数据格式

- 重写ViewBinder的setViewValue，在其中用if做一个判断，如果cursor选取的内容为时间，就进行自定义的操作，修改时间的格式
```java
 /**实现最近修改时间的格式化*/
    void changeDateFormat(SimpleCursorAdapter adapter){
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            //重写ViewBinder的setViewValue方法
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE) == columnIndex){
                    //设置时间格式
                    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                    //创建Date对象，获取时间
                    Date date = new Date(cursor.getLong(columnIndex));
                    //转换为字符串
                    String time = format.format(date);
                    //数据绑定
                    TextView textView = (TextView)view;
                    textView.setText(time);
                    return true;
                }
                return false;
            }
        });
    }
```

- 调用这个方法就可完成对时间的格式转换

![image](http://chuantu.xyz/t6/737/1590919485x989559068.png)

## 二、添加笔记查询功能

### 1.在menu菜单中添加一个searchview
```XML
<!--  search  -->
    <item android:id="@+id/menu_search"
        android:title="search"
        android:actionViewClass="android.widget.SearchView"
        android:showAsAction="always" />
```
### 2.在契约类NotePad中添加查询的条件语句
```java
//按标题查询
        public static final String SELECT_BY_TITLE = COLUMN_NAME_TITLE + " like ?";
```

### 3.在初始化菜单的时候进行配置,添加搜索事件监听
```java
SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        //设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            // 当点击搜索按钮时触发该方法
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            // 当搜索内容改变时触发该方法，时刻监听输入搜索框的值
            @Override
            public boolean onQueryTextChange(String newText) {
                //设置where语句
                String[] selectionArgs = {"%" + newText + "%"};

                Cursor cursor = managedQuery(
                        getIntent().getData(),            // Use the default content URI for the provider.
                        PROJECTION,                       // Return the note ID and title for each note.
                        NotePad.Notes.SELECT_BY_TITLE,                           // No where clause, return all records.
                        selectionArgs,                           // No where clause, therefore no where column values.
                        NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
                );

                /*
                 * The following two arrays create a "map" between columns in the cursor and view IDs
                 * for items in the ListView. Each element in the dataColumns array represents
                 * a column name; each element in the viewID array represents the ID of a View.
                 * The SimpleCursorAdapter maps them in ascending order to determine where each column
                 * value will appear in the ListView.
                 */

                // The names of the cursor columns to display in the view, initialized to the title column
                String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE } ;

                // The view IDs that will display the cursor columns, initialized to the TextView in
                // noteslist_item.xml
                int[] viewIDs = { android.R.id.text1,R.id.text2};

                // Creates the backing adapter for the ListView.
                SimpleCursorAdapter adapter
                        = new SimpleCursorAdapter(
                        getApplicationContext(),                             // The Context for the ListView
                        R.layout.noteslist_item,          // Points to the XML for a list item
                        cursor,                           // The cursor to get items from
                        dataColumns,
                        viewIDs
                );
                changeDateFormat(adapter);
                // Sets the ListView's adapter to be the cursor adapter that was just created.
                setListAdapter(adapter);
                return true;
            }
        });
```

- 最终效果

![image](http://chuantu.xyz/t6/737/1590920402x989559068.png)
![image](http://chuantu.xyz/t6/737/1590920422x989499252.png)
![image](http://chuantu.xyz/t6/737/1590920443x989499252.png)
