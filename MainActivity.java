package com.example.calculator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // UI Components
    private TextView result_tv, solution_tv, historyTextView;
    private MaterialButton buttonHistory, button7, button8, buttondivide, buttonplus, buttonminus, buttonmultiply, buttonequal;
    private MaterialButton button27, button11, button12, button13, button16, button17, button18, button21, button22, button23;
    private MaterialButton button26, button28;
    private MaterialButton button1, button2, button3, button4, button5, button9, button14, button19, button24;
    private Button btn_voice;

    // Database Helper
    private DatabaseHelper databaseHelper;

    // Voice Assistant Components
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 200;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Initialize TextViews
        result_tv = findViewById(R.id.result_tv);
        solution_tv = findViewById(R.id.solution_tv);
        historyTextView = findViewById(R.id.history_text_view);

        // Initialize Buttons
        initializeButtons();

        // Initialize Text-to-Speech
        initializeTextToSpeech();

        // Check and Request Microphone Permission if not already granted
        checkAndRequestPermissions();
    }

    /**
     * Initializes all the buttons and sets their onClick listeners.
     */
    private void initializeButtons() {
        btn_voice = findViewById(R.id.btn_voice);
        buttonHistory = findViewById(R.id.buttonHistory);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        buttondivide = findViewById(R.id.buttondivide);
        buttonplus = findViewById(R.id.buttonplus);
        buttonminus = findViewById(R.id.buttonminus);
        buttonmultiply = findViewById(R.id.buttonmultiply);
        buttonequal = findViewById(R.id.buttonequal);
        button27 = findViewById(R.id.button27);
        button11 = findViewById(R.id.button11);
        button12 = findViewById(R.id.button12);
        button13 = findViewById(R.id.button13);
        button16 = findViewById(R.id.button16);
        button17 = findViewById(R.id.button17);
        button18 = findViewById(R.id.button18);
        button21 = findViewById(R.id.button21);
        button22 = findViewById(R.id.button22);
        button23 = findViewById(R.id.button23);
        button26 = findViewById(R.id.button26);
        button28 = findViewById(R.id.button28);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button9 = findViewById(R.id.button9);
        button14 = findViewById(R.id.button14);
        button19 = findViewById(R.id.button19);
        button24 = findViewById(R.id.button24);

        // Set OnClickListeners for all buttons
        btn_voice.setOnClickListener(this);
        buttonHistory.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        buttondivide.setOnClickListener(this);
        buttonplus.setOnClickListener(this);
        buttonminus.setOnClickListener(this);
        buttonmultiply.setOnClickListener(this);
        buttonequal.setOnClickListener(this);
        button27.setOnClickListener(this);
        button11.setOnClickListener(this);
        button12.setOnClickListener(this);
        button13.setOnClickListener(this);
        button16.setOnClickListener(this);
        button17.setOnClickListener(this);
        button18.setOnClickListener(this);
        button21.setOnClickListener(this);
        button22.setOnClickListener(this);
        button23.setOnClickListener(this);
        button26.setOnClickListener(this);
        button28.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button9.setOnClickListener(this);
        button14.setOnClickListener(this);
        button19.setOnClickListener(this);
        button24.setOnClickListener(this);
    }

    /**
     * Initializes the TextToSpeech engine.
     */
    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(MainActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Initialization Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks and requests RECORD_AUDIO permission at runtime.
     */
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onClick(View view) {
        // Handle Voice Button Click
        if (view.getId() == R.id.btn_voice) {
            promptSpeechInput();
            return;
        }

        // Handle History Button Click
        if (view.getId() == R.id.buttonHistory) {
            showHistory();
            return;
        }

        // Cast the view to MaterialButton to retrieve its text
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String dataToCalculate = solution_tv.getText().toString();

        // Handle AC (All Clear) Button
        if (buttonText.equals("AC")) {
            solution_tv.setText("");
            result_tv.setText("0");
            return;
        }

        // Handle Equals Button
        if (buttonText.equals("=")) {
            String finalResult = getResult(dataToCalculate);
            if (!finalResult.equals("Error")) {
                result_tv.setText(finalResult);
                solution_tv.setText(finalResult);
                // Save calculation history to database
                databaseHelper.addHistory(dataToCalculate, finalResult);
                // Speak the result
                textToSpeech.speak("The result is " + finalResult, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                result_tv.setText("Error");
                textToSpeech.speak("Error in calculation", TextToSpeech.QUEUE_FLUSH, null, null);
            }
            return;
        }

        // Handle Trigonometric Functions
        if (buttonText.equalsIgnoreCase("sin") || buttonText.equalsIgnoreCase("cos") || buttonText.equalsIgnoreCase("tan")) {
            String result = calculateTrigFunction(buttonText.toLowerCase(), dataToCalculate);
            if (!result.equals("Error")) {
                result_tv.setText(result);
                solution_tv.setText(result);
                // Save calculation history to database
                databaseHelper.addHistory(buttonText.toLowerCase() + "(" + dataToCalculate + ")", result);
                // Speak the result
                textToSpeech.speak("The result is " + result, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                result_tv.setText("Error");
                textToSpeech.speak("Error in calculation", TextToSpeech.QUEUE_FLUSH, null, null);
            }
            return;
        }

        // Handle Additional Functions
        if (buttonText.equalsIgnoreCase("log") || buttonText.equalsIgnoreCase("√") || buttonText.equalsIgnoreCase("x²") ||
                buttonText.equalsIgnoreCase("x³") || buttonText.equalsIgnoreCase("!x") || buttonText.equalsIgnoreCase("10^x")) {
            String functionKey = buttonText;
            String value = dataToCalculate;

            // For factorial, ensure the input is an integer
            if (functionKey.equals("!x")) {
                try {
                    int intValue = Integer.parseInt(value);
                    String result = String.valueOf(factorial(intValue));
                    if (!result.equals("Error")) {
                        result_tv.setText(result);
                        solution_tv.setText(result);
                        // Save calculation history to database
                        databaseHelper.addHistory(functionKey + "(" + value + ")", result);
                        // Speak the result
                        textToSpeech.speak("The result is " + result, TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        result_tv.setText("Error");
                        textToSpeech.speak("Error in calculation", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                } catch (NumberFormatException e) {
                    result_tv.setText("Error");
                    textToSpeech.speak("Invalid input for factorial", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            } else {
                String result = calculateAdditionalFunction(functionKey, value);
                if (!result.equals("Error")) {
                    result_tv.setText(result);
                    solution_tv.setText(result);
                    // Save calculation history to database
                    databaseHelper.addHistory(functionKey + "(" + value + ")", result);
                    // Speak the result
                    textToSpeech.speak("The result is " + result, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    result_tv.setText("Error");
                    textToSpeech.speak("Error in calculation", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
            return;
        }

        // Append button text to the current input
        dataToCalculate += buttonText;
        solution_tv.setText(dataToCalculate);
    }

    /**
     * Prompts the user to speak their calculation.
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your calculation");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry, your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the result from the speech input intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String spokenText = result.get(0);
                    processVoiceInput(spokenText);
                } else {
                    Toast.makeText(this, "No speech recognized", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Speech recognition canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Processes the spoken input, evaluates it, displays the result, and speaks it aloud.
     * @param spokenText The text recognized from speech input.
     */
    private void processVoiceInput(String spokenText) {
        if (spokenText == null || spokenText.trim().isEmpty()) {
            Toast.makeText(this, "No speech recognized", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert spoken operators and keywords to symbols
        String expression = spokenText.toLowerCase()
                .replaceAll("plus|add", "+")
                .replaceAll("minus|subtract", "-")
                .replaceAll("multiply by|multiplied by|times", "*")
                .replaceAll("divide by|divided by|over", "/")
                .replaceAll("power of|to the power of|raise to", "^")
                .replaceAll("open parenthesis|left parenthesis", "(")
                .replaceAll("close parenthesis|right parenthesis", ")")
                .replaceAll("square of|x squared", "^2")  // Handle square
                .replaceAll("cube of|x cubed", "^3");      // Handle cube       // Handle square root

        // Handle function-based inputs (trig, log, sqrt, factorial)
        if (expression.startsWith("sin") || expression.startsWith("cos") || expression.startsWith("tan") ||
                expression.startsWith("log") || expression.startsWith("sqrt") || expression.startsWith("factorial") ||
                expression.startsWith("cbrt")) {  // Adding cube root (cbrt) case

            // Extract the function and its argument using regex
            Pattern pattern = Pattern.compile("(sin|cos|tan|log|sqrt|factorial|cbrt)\\s*\\(?\\s*(\\d+(\\.\\d+)?)\\s*\\)?");
            Matcher matcher = pattern.matcher(expression);
            if (matcher.find()) {
                String function = matcher.group(1);
                String value = matcher.group(2);
                handleFunctionInput(function, value);
                return;
            }
        }

        // Replace '^' with 'Math.pow' for correct evaluation in the expression evaluator
        expression = replacePowerOperator(expression);

        // Set the expression to the TextView
        solution_tv.setText(expression);

        // Evaluate the expression
        String finalResult = getResult(expression);
        if (!finalResult.equals("Error")) {
            result_tv.setText(finalResult);
            // Save calculation history to database
            databaseHelper.addHistory(expression, finalResult);
            // Speak the result
            textToSpeech.speak("The result is " + finalResult, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            result_tv.setText("Error");
            textToSpeech.speak("Error in calculation", TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
    /**
     * Handles function-based inputs like sin, cos, tan, log, sqrt, and factorial.
     * @param function The mathematical function to apply.
     * @param value The input value for the function.
     */
    private void handleFunctionInput(String function, String value) {
        String result;
        switch (function) {
            case "sin":
            case "cos":
            case "tan":
                result = calculateTrigFunction(function, value);
                break;
            case "log":
            case "sqrt":
                result = calculateAdditionalFunction(function.equals("sqrt") ? "√" : "log", value);
                break;
            case "factorial":
                try {
                    int intValue = Integer.parseInt(value);
                    result = String.valueOf(factorial(intValue));
                } catch (NumberFormatException e) {
                    result = "Error";
                }
                break;
            default:
                result = "Error";
        }

        if (!result.equals("Error")) {
            result_tv.setText(result);
            solution_tv.setText(result);
            // Save calculation history to database
            databaseHelper.addHistory(function + "(" + value + ")", result);
            // Speak the result
            textToSpeech.speak("The result is " + result, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            result_tv.setText("Error");
            textToSpeech.speak("Error in calculation", TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    /**
     * Replaces the '^' operator with 'Math.pow(a,b)' for correct evaluation in Rhino.
     * @param expression The original mathematical expression.
     * @return The modified expression with 'Math.pow' instead of '^'.
     */
    private String replacePowerOperator(String expression) {
        // Regular expression to find patterns like a^b where a and b are numbers
        Pattern pattern = Pattern.compile("([\\d\\.]+)\\s*\\^\\s*([\\d\\.]+)");
        Matcher matcher = pattern.matcher(expression);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String base = matcher.group(1);
            String exponent = matcher.group(2);
            String replacement = "Math.pow(" + base + "," + exponent + ")";
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Evaluates the mathematical expression using Rhino JavaScript engine.
     * @param data The mathematical expression to evaluate.
     * @return The result as a string or "Error" if evaluation fails.
     */
    private String getResult(String data) {
        try {
            data = data
                    .replaceAll("x\\^2", "Math.pow(x, 2)")
                    .replaceAll("x\\^3", "Math.pow(x, 3)")
                    .replaceAll("10\\^", "Math.pow(10, ")
                    .replaceAll("sqrt\\(([^)]+)\\)", "Math.sqrt($1)");

            Context context = Context.enter();
            context.setOptimizationLevel(-1); // Necessary for Android compatibility
            Scriptable scriptable = context.initStandardObjects();
            // Evaluate the expression
            Object result = context.evaluateString(scriptable, data, "JavaScript", 1, null);
            String finalResult = Context.toString(result);
            // Remove trailing '.0' if the result is an integer
            if (finalResult.endsWith(".0")) {
                finalResult = finalResult.substring(0, finalResult.length() - 2);
            }
            return finalResult;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        } finally {
            Context.exit();
        }
    }

    /**
     * Calculates trigonometric functions.
     * @param function The trigonometric function (sin, cos, tan).
     * @param value The input value in degrees.
     * @return The result as a string or "Error" if calculation fails.
     */
    private String calculateTrigFunction(String function, String value) {
        try {
            double input = Double.parseDouble(value);
            double result;

            switch (function) {
                case "sin":
                    result = Math.sin(Math.toRadians(input));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(input));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(input));
                    break;
                default:
                    return "Error";
            }

            return String.valueOf(result);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    /**
     * Calculates additional mathematical functions.
     * @param function The function to calculate (log, √, x², etc.).
     * @param value The input value.
     * @return The result as a string or "Error" if calculation fails.
     */
    private String calculateAdditionalFunction(String function, String value) {
        try {
            double input = Double.parseDouble(value);
            double result;

            switch (function) {
                case "log":
                    if (input <= 0) return "Error";
                    result = Math.log10(input);
                    break;
                case "√":
                    if (input < 0) return "Error";
                    result = Math.sqrt(input);
                    break;
                case "x²":
                    result = Math.pow(input, 2);
                    break;
                case "x³":
                    result = Math.pow(input, 3);
                    break;
                case "10^x":
                    result = Math.pow(10, input);
                    break;
                default:
                    return "Error";
            }

            return String.valueOf(result);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    /**
     * Calculates the factorial of a number.
     * @param number The number to calculate the factorial of.
     * @return The factorial as a double, or Double.NaN if the number is negative.
     */
    private double factorial(int number) {
        if (number < 0) {
            return Double.NaN;
        }
        double result = 1;
        for (int i = 1; i <= number; i++) {
            result *= i;
            // Prevent overflow
            if (Double.isInfinite(result)) {
                return Double.NaN;
            }
        }
        return result;
    }

    /**
     * Displays the calculation history from the database.
     */
    private void showHistory() {
        Cursor cursor = databaseHelper.getAllHistory();
        StringBuilder historyText = new StringBuilder();

        if (cursor != null) {
            try {
                int expressionIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EXPRESSION);
                int resultIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RESULT);

                if (expressionIndex == -1 || resultIndex == -1) {
                    historyText.append("Error: Columns not found");
                } else {
                    if (cursor.moveToFirst()) {
                        do {
                            String expression = cursor.getString(expressionIndex);
                            String result = cursor.getString(resultIndex);
                            historyText.append(expression).append(" = ").append(result).append("\n");
                        } while (cursor.moveToNext());
                    } else {
                        historyText.append("No history available");
                    }
                }
            } finally {
                cursor.close(); // Ensure cursor is closed to avoid memory leaks
            }
        } else {
            historyText.append("Error: No data available");
        }

        historyTextView.setText(historyText.toString());
    }

    /**
     * Handles the result of permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_RECORD_AUDIO){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Microphone Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission Denied for Microphone", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Releases the TextToSpeech resources when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
