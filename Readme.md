# Лабораторная работа №6. Многопоточные Android приложения.

# Цели
Получить практические навыки разработки многопоточных приложений:

1. Организация обработки длительных операций в background (worker) thread:
* Запуск фоновой операции (coroutine/asynctask/thread)
* Остановка фоновой операции (coroutine/asynctask/thread)
2. Публикация данных из background (worker) thread в main (ui) thread.

Освоить 3 основные группы API для разработки многопоточных приложений:
1. Kotlin Coroutines
2. AsyncTask
3. Java Threads

# Выполнение работы

## Задача 1. Альтернативные решения задачи "не секундомер" из Лаб. 2
Используйте приложение "не секундомер", получившееся в результате выполнениня Лабораторной работы №2. Разработайте несколько альтернативных приложений "не секундомер", отличающихся друг от друга организацией многопоточной работы. Опишите все известные Вам решения.

Реализованы 3 решения:
1. AsyncTask
2. Kotlin Coroutine
3. Java Threads


#### Листинг 1.1 AsyncTask
```Kotlin
class AsyncTask : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var asyncTask: MyAsyncTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task1)
    }

    override fun onStart() {
        asyncTask = MyAsyncTask()
        asyncTask.execute()
        super.onStart()
    }

    override fun onStop() {
        asyncTask.cancel(true)
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seconds", secondsElapsed)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        secondsElapsed = savedInstanceState.getInt("seconds")
        textSecondsElapsed.post {
            textSecondsElapsed.text = "Seconds elapsed: $secondsElapsed"
        }
    }

    inner class MyAsyncTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            while (!isCancelled) {
                TimeUnit.SECONDS.sleep(1)
                publishProgress()
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
            textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
        }

    }
}
```

#### Листинг 1.2 Coroutine
```Kotlin
class Coroutine : AppCompatActivity() {
    private var secondsElapsed: Int = 0
    private lateinit var coroutine: Job
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task1)

    }

    override fun onStart() {
        super.onStart()
        coroutine = scope.launch {
            while (true) {
                delay(1000)
                textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
            }
        }
    }

    override fun onStop() {
        super.onStop()
        coroutine.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seconds", secondsElapsed)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        secondsElapsed = savedInstanceState.getInt("seconds")
        textSecondsElapsed.post {
            textSecondsElapsed.text = "Seconds elapsed: $secondsElapsed"
        }
    }
}
```
#### Листинг 1.3 Threads
```Kotlin
class Threads : AppCompatActivity() {
    var secondsElapsed: Int = 0
    var work = true
    var backgroundThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task1)
    }

    override fun onResume() {
        super.onResume()

        backgroundThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                Thread.sleep(1000)
                
                textSecondsElapsed.post {
                    textSecondsElapsed.text = "Seconds elapsed: " + secondsElapsed++
                }
                if (!work) {
                    Thread.currentThread().interrupt()
                }
            }
        }
        backgroundThread!!.start()
        work = true

    }

    override fun onPause() {
        super.onPause()
        work = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("seconds", secondsElapsed)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        secondsElapsed = savedInstanceState.getInt("seconds")
        textSecondsElapsed.post {
            textSecondsElapsed.text = "Seconds elapsed: $secondsElapsed"
        }
    }
}
```

## Задача 2. Загрузка картинки в фоновом потоке (AsyncTask)
Создайте приложение, которое скачивает картинку из интернета и размещает ее в ImageView в Activity. За основу возьмите [код со StackOverflow.](https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android/9288544#9288544)

Ниже приведен layout для решения задачи с одной кнопкой и imageView(Использую этот layout в следующих задачах).
#### Листинг 2.1 activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/everything"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity">


        <Button
            android:id="@+id/button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="@string/download"
            app:layout_constraintBottom_toBottomOf="@+id/ImageView"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent" />

        <ImageView
            android:id="@+id/ImageView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:contentDescription="@android:string/copy"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />


    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```

Было создано приложение с AsyncTask. Большую часть кода создавать не пришлось так как код взят со StackOverflow.
Класс DownloadImageTask скопирован без изменения.
#### Листинг 2.2 MainActivity.java
```Java
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DownloadImageTask(binding.ImageView)
                                .execute("https://cdn.lifehacker.ru/wp-content/uploads/2018/04/15-skrytyx-fishek-Android_1523567204-1140x570.jpg");
                    }
                });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            binding.ImageView.setImageBitmap(result);
        }
    }
}
```
После нажатия на кнопку мы получим картинку.
![alt text]( https://github.com/3oDoR/Lab6Android/blob/main/img/task2.png "task2")


## Задача 2.3 Загрузка картинки в фоновом потоке (Kotlin Coroutines)  
Аналогично, добавлена загрузка картинки используя корутины.Тут скачивание картинки было отправлено на Dispatcher. IO
```Kotlin
class Coroutine : AppCompatActivity() {
    private val Scope = CoroutineScope(Dispatchers.Main)

    private fun DownloadImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val inputStream = URL(url).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("Error", e.message.orEmpty())
            e.printStackTrace()
        }

        return bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            Scope.launch(Dispatchers.IO) {
                val image = DownloadImage("https://miro.medium.com/max/632/1*aJp" +
                        "-LNY8Zeb5gyCKdEWMfQ.png")
                launch(Dispatchers.Main) {
                    binding.ImageView.setImageBitmap(image)
                }
            }
        }
    }
}
```

После нажатия на кнопку мы получим картинку.
![alt text]( https://github.com/3oDoR/Lab6Android/blob/main/img/task3.png "task3")

## Задача 4. Использование сторонних библиотек
Для решения данной задачи я выбрал библиотеку [picasso](https://square.github.io/picasso/). 

Разбираться почти не пришлось так как на сайте данной библиотеки сразу приведен шаблон кода для скачивания изображения. 

#### Листинг 4.1 PicassoLib
```Java
public class PicassoLib extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load("https://lh3.googleusercontent.com/" +
                                "2IQ9psSDzqfFk4o5aguHbYc5ee2iBLfzZddy0eXtAIvVbq9og11TBG" +
                                "Qq8I2Ct7upyB4-FwEULww6gdMVGJjVqhxrxp87x5jE66lcYvgLBTF6" +
                                "MAN1bIbHvOOZQ8iOw55KXtuniboC").into(binding.ImageView);
                    }
                });
    }

```

После нажатия на кнопку мы получим картинку.
![alt text]( https://github.com/3oDoR/Lab6Android/blob/main/img/task4.png "task4")


## Выводы
1 Задача - 3 часа - Большая часть времени ушла на разбирание и поиск материала.
2 Задача - 2 часа - Почти все время ушло на разбор кода на StackOverflow.
3 Задача - 3 часа - Большая часть времени ушла на разбирание и поиск материала.
4 Задача - 30 минут(Пробывал различные методы и примеры указанные на сайте библиотеки) - 
Оказалась довольно простой и показала,что не всегда нужно пытаться все написать с нуля,
 а можно  использовать библиотеки.

