package fr.pops.peip2.android.example.knoweachother;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button whoAnswersOKButton;
    private Button whoGuessesOKButton;
    private Button button_false;
    private Button button_true;
    private TextView Quest;
    private TextView nbQuest;
    private int currentQuestionIndex = 1;
    private boolean[] answers;
    private boolean[] player2Answers;
    private boolean isFirstPlayerTurn = true;
    private int currentScore = 0;
    private String whoAnswersName = "";
    private String whoGuessesName = "";
    private EditText whoAnswersText;
    private EditText whoGuessesText;
    private TextView whoSpeaks;
    private String questionKey;
    private EditText hintInput;
    private Button showHintButton;
    private TextView hintDisplay;
    private String[] hints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        whoAnswersOKButton = (Button) findViewById(R.id.whoAnswersOKButton);
        whoGuessesOKButton = (Button) findViewById(R.id.whoGuessesOKButton);
        button_false = (Button) findViewById(R.id.button_false);
        button_true = (Button) findViewById(R.id.button_true);
        Quest = (TextView) findViewById(R.id.question);
        nbQuest = (TextView) findViewById(R.id.nbQuestion);
        answers = new boolean[3];
        player2Answers = new boolean[3];

        whoAnswersText = findViewById(R.id.whoAnswersText);
        whoGuessesText = findViewById(R.id.whoGuessesText);
        whoSpeaks = findViewById(R.id.whoSpeaks);

        button_true.setEnabled(false);
        button_false.setEnabled(false);

        whoSpeaks.setText("");
        Quest.setText("");

        hintInput = findViewById(R.id.hintInput);
        showHintButton = findViewById(R.id.showHintButton);
        hintDisplay = findViewById(R.id.hintDisplay);
        hints = new String[answers.length];


        whoGuessesOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGuesser(v);
            }
        });

        whoAnswersOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAnswerer(v);
            }
        });

        updateQuestion();

        button_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processAnswer(false);
            }
        });
        button_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processAnswer(true);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void processAnswer(Boolean choice) {
        if (isFirstPlayerTurn) {
            saveHint();
            answers[currentQuestionIndex - 1] = choice;
        } else {
            player2Answers[currentQuestionIndex - 1] = choice;
            boolean isCorrect = answers[currentQuestionIndex - 1] == choice;
            if (isCorrect) {
                currentScore++;
            }
            String feedback = isCorrect ? getString(R.string.correct_answer) : getString(R.string.wrong_answer);
            Toast.makeText(this, feedback, Toast.LENGTH_SHORT).show();
        }

        currentQuestionIndex++;

        if (currentQuestionIndex > answers.length) {
            if (isFirstPlayerTurn) {
                isFirstPlayerTurn = false;
                currentQuestionIndex = 1;
                changePlayer();
            } else {
                endGame();
            }
        } else {
            updateQuestion();
        }

    }

    private void updateQuestion() {
        if (currentQuestionIndex > answers.length) {
            Quest.setText(getString(R.string.question_not_found));
            return;
        }
        if (isFirstPlayerTurn) {
            questionKey = "question_" + currentQuestionIndex + "_ans";
            hintInput.setVisibility(View.VISIBLE);
            showHintButton.setVisibility(View.GONE);
            hintDisplay.setVisibility(View.GONE);
        } else {
            questionKey = "question_" + currentQuestionIndex;
            hintInput.setVisibility(View.GONE);
            configureHintDisplay();
        }
        int questionResId = getResources().getIdentifier(
                questionKey,
                "string",
                getPackageName()
        );

        if (questionResId != 0) {
            String formattedQuestion = getString(questionResId, whoAnswersName);
            Quest.setText(formattedQuestion);
        } else {
            Quest.setText(getString(R.string.question_not_found));
        }
        String nbQuestionText = getString(R.string.question_prefix) + " " + currentQuestionIndex;
        nbQuest.setText(nbQuestionText);
//        if (nbQuest != null && nbQuest.getText() != null) {
//            CharSequence actuel = nbQuest.getText();
//            if (actuel.length() > 0) {
//                char last = actuel.charAt(actuel.length() - 1);
//                last++;
//                CharSequence test = last + "";
//                actuel = actuel.subSequence(0, actuel.length() - 1);
//                String fin = actuel.toString() + test;
//                nbQuest.setText(fin);
//            }
//        }
    }

    private void handleGuesser(View v) {
        whoGuessesName = whoGuessesText.getText().toString().trim();
        areTextFilled();
        if (!whoGuessesName.isEmpty()) {
            whoGuessesOKButton.setEnabled(false);
            if (!whoAnswersName.isEmpty()) {
                whoSpeaks.setText(getString(R.string.answerer, whoAnswersName));
                updateQuestion();
            }
        } else {
            whoGuessesText.setError(getString(R.string.valid_name_error));
        }
    }

    private void handleAnswerer(View v) {
        whoAnswersName = whoAnswersText.getText().toString().trim();
        areTextFilled();
        String message = String.format(getString(R.string.answerer), whoAnswersName);
        whoSpeaks.setText(message);
        if (!whoAnswersName.isEmpty()) {
            whoAnswersOKButton.setEnabled(false);
            if (!whoGuessesName.isEmpty()) {
                whoSpeaks.setText(getString(R.string.answerer, whoAnswersName));
            }
        } else {
            whoAnswersText.setError(getString(R.string.valid_name_error));
        }
    }

    private void changePlayer() {
        String message = String.format(getString(R.string.guesser), whoGuessesName);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        whoSpeaks.setText(message);
        currentQuestionIndex = 1;

        updateQuestion();
    }

    private void endGame() {
//        String finalMessage = String.format(
//                getString(R.string.player_score),
//                whoGuessesName,
//                currentScore,
//                answers.length
//        );
//        Toast.makeText(this, finalMessage, Toast.LENGTH_LONG).show();
//        new android.os.Handler().postDelayed(this::finish, 5000);

        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
        String[] questions = new String[] {
                getString(R.string.question_1_ans),
                getString(R.string.question_2_ans),
                getString(R.string.question_3_ans)
        };

        if (answers == null || player2Answers == null || answers.length != questions.length || player2Answers.length != questions.length) {
            Log.e("EndGame", "Data arrays are null or mismatched in length");
            return; // Empêche l'application de planter
        }
        Log.d("EndGame", "Questions: " + Arrays.toString(questions));
        Log.d("EndGame", "Player 1 Answers: " + Arrays.toString(answers));
        Log.d("EndGame", "Player 2 Answers: " + Arrays.toString(player2Answers));

        intent.putExtra("questions", questions);
        intent.putExtra("player1Answers", answers);
        intent.putExtra("player2Answers", player2Answers);
        startActivity(intent);
        finish();
    }

    private void areTextFilled() {
        if (!whoGuessesName.isEmpty() && !whoAnswersName.isEmpty()) {
            button_false.setEnabled(true);
            button_true.setEnabled(true);
            findViewById(R.id.hintInput).setEnabled(true);
        }
    }

    private void saveHint() {
        String currentHint = hintInput.getText().toString().trim();
        hints[currentQuestionIndex - 1] = currentHint;
        hintInput.setText("");
    }

    private void configureHintDisplay() {
        showHintButton.setVisibility(View.VISIBLE);
        hintDisplay.setVisibility(View.GONE);

        showHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentHint = hints[currentQuestionIndex - 1];
                if (currentHint != null && !currentHint.isEmpty()) {
                    hintDisplay.setVisibility(View.VISIBLE);
                    hintDisplay.setText(getString(R.string.hint_label, currentHint));
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_hint_available, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

