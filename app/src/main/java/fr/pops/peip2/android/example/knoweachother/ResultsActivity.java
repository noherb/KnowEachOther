package fr.pops.peip2.android.example.knoweachother;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {

    private static final int MAX_QUESTION_LENGTH = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        TableLayout tableLayout = findViewById(R.id.resultsTable);
        Button restartGameButton = findViewById(R.id.restartGameButton);

        String[] questions = getIntent().getStringArrayExtra("questions");
        boolean[] player1Answers = getIntent().getBooleanArrayExtra("player1Answers");
        boolean[] player2Answers = getIntent().getBooleanArrayExtra("player2Answers");

        if (questions == null || player1Answers == null || player2Answers == null) {
            Log.e("ResultsActivity", "Data received is null");
            return;
        }

        if (questions.length != player1Answers.length || questions.length != player2Answers.length) {
            Log.e("ResultsActivity", "Mismatch in data sizes");
            return;
        }

        if (questions != null && player1Answers != null && player2Answers != null) {
            for (int i = 0; i < questions.length; i++) {
                TableRow row = new TableRow(this);

                String shortenedQuestion = shortenText(questions[i], MAX_QUESTION_LENGTH);

                TextView questionIndex = new TextView(this);
                questionIndex.setText(String.valueOf(i + 1));
                questionIndex.setPadding(8, 8, 8, 8);
                row.addView(questionIndex);

                TextView question = new TextView(this);
                question.setText(shortenedQuestion);
                question.setPadding(8, 8, 8, 8);
                row.addView(question);

                TextView player1Answer = new TextView(this);
                player1Answer.setText(player1Answers[i] ? getText(R.string.ans_true) : getText(R.string.ans_false));
                player1Answer.setPadding(8, 8, 8, 8);
                row.addView(player1Answer);

                TextView player2Answer = new TextView(this);
                player2Answer.setText(player2Answers[i] ? getText(R.string.ans_true) : getText(R.string.ans_false));
                player2Answer.setPadding(8, 8, 8, 8);
                row.addView(player2Answer);

                if (player1Answers[i] == player2Answers[i]) {
                    row.setBackgroundColor(Color.parseColor("#A5D6A7"));
                } else {
                    row.setBackgroundColor(Color.parseColor("#EF9A9A"));
                }

                tableLayout.addView(row);
            }
        }

        restartGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private String shortenText(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength - 3) + "...";
        }
        return text;
    }
}
