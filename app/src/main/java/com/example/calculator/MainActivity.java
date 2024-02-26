package com.example.calculator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private StringBuilder input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        input = new StringBuilder();

        setNumberButtonClickListeners();
        setOperatorButtonClickListeners();
    }

    private void setNumberButtonClickListeners() {
        int[] numberButtonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6, R.id.button7,
                R.id.button8, R.id.button9
        };

        for (int numberButtonId : numberButtonIds) {
            Button button = findViewById(numberButtonId);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button clickedButton = (Button) v;
                    input.append(clickedButton.getText());
                    textView.setText(input.toString());
                }
            });
        }
    }

    private void setOperatorButtonClickListeners() {
        int[] operatorButtonIds = {
                R.id.buttonAdd, R.id.buttonSubtract, R.id.buttonMultiply,
                R.id.buttonDivide, R.id.buttonleftbracket, R.id.buttonrightbracket, R.id.buttonCommas
        };

        for (int operatorButtonId : operatorButtonIds) {
            Button button = findViewById(operatorButtonId);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button clickedButton = (Button) v;
                    input.append(clickedButton.getText());
                    textView.setText(input.toString());
                }
            });
        }


        // Equals button
        Button equalsButton = findViewById(R.id.buttonEquals);
        equalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expression = input.toString();
                try {
                    double result = evaluateExpression(expression);
                    DecimalFormat df = new DecimalFormat("#.###");
                    textView.setText(df.format(result));
                } catch (ArithmeticException e) {
                    textView.setText("Error");
                }
            }
        });
        Button button = findViewById(R.id.buttonCommas);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clickedButton = (Button) v;
                if (clickedButton.getId() == R.id.buttonCommas) {
                    input.append(".");
                } else {
                    input.append(clickedButton.getText());
                }
                textView.setText(input.toString());
            }
        });

        // Clear button
        Button clearButton = findViewById(R.id.buttonClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setLength(0);
                textView.setText("0");
            }
        });
    }

    private double evaluateExpression(String expression) {
        String[] tokens = expression.split("(?<=[-+*/()])|(?=[-+*/()])");
        return evaluateExpression(tokens);
    }

    private double evaluateExpression(String[] tokens) {
        return evaluate(tokens, 0, tokens.length - 1);
    }

    private double evaluate(String[] tokens, int start, int end) {
        int depth = 0;
        int currentOperator = -1;
        int minPrecedence = Integer.MAX_VALUE;

        for (int i = start; i <= end; i++) {
            String token = tokens[i];
            if ("(".equals(token)) {
                depth++;
            } else if (")".equals(token)) {
                depth--;
            } else if (depth == 0 && ("+".equals(token) || "-".equals(token)) && getPrecedence(token) <= minPrecedence) {
                currentOperator = i;
                minPrecedence = getPrecedence(token);
            } else if (depth == 0 && ("*".equals(token) || "/".equals(token)) && getPrecedence(token) < minPrecedence) {
                currentOperator = i;
                minPrecedence = getPrecedence(token);
            }
        }

        if (currentOperator != -1) {
            double leftOperand = evaluate(tokens, start, currentOperator - 1);
            double rightOperand = evaluate(tokens, currentOperator + 1, end);
            switch (tokens[currentOperator]) {
                case "+":
                    return leftOperand + rightOperand;
                case "-":
                    return leftOperand - rightOperand;
                case "*":
                    return leftOperand * rightOperand;
                case "/":
                    if (rightOperand == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return leftOperand / rightOperand;
            }
        }

        if ("(".equals(tokens[start]) && ")".equals(tokens[end])) {
            return evaluate(tokens, start + 1, end - 1);

        }

        return Double.parseDouble(tokens[start]);
    }

    private int getPrecedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }
}

