package com.smirnov.lab6android.task4;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import com.smirnov.lab6android.R;
import com.smirnov.lab6android.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

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

}