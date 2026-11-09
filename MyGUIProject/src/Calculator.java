import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame implements ActionListener {

    JTextField display;
    JButton b1, b2, b3, b4, b5, b6, b7, b8, b9, b0;
    JButton add, sub, mul, div, equal, clear;

    double num1, num2, result;
    char operator;

    Calculator() {

        setTitle("Calculator");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        display = new JTextField();
        add(display, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));

        b1 = new JButton("1");
        b2 = new JButton("2");
        b3 = new JButton("3");
        add = new JButton("+");

        b4 = new JButton("4");
        b5 = new JButton("5");
        b6 = new JButton("6");
        sub = new JButton("-");

        b7 = new JButton("7");
        b8 = new JButton("8");
        b9 = new JButton("9");
        mul = new JButton("*");

        b0 = new JButton("0");
        clear = new JButton("C");
        equal = new JButton("=");
        div = new JButton("/");

        panel.add(b1);
        panel.add(b2);
        panel.add(b3);
        panel.add(add);

        panel.add(b4);
        panel.add(b5);
        panel.add(b6);
        panel.add(sub);

        panel.add(b7);
        panel.add(b8);
        panel.add(b9);
        panel.add(mul);

        panel.add(b0);
        panel.add(clear);
        panel.add(equal);
        panel.add(div);

        add(panel, BorderLayout.CENTER);

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        b4.addActionListener(this);
        b5.addActionListener(this);
        b6.addActionListener(this);
        b7.addActionListener(this);
        b8.addActionListener(this);
        b9.addActionListener(this);
        b0.addActionListener(this);

        add.addActionListener(this);
        sub.addActionListener(this);
        mul.addActionListener(this);
        div.addActionListener(this);
        equal.addActionListener(this);
        clear.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == b1)
            display.setText(display.getText() + "1");

        if (e.getSource() == b2)
            display.setText(display.getText() + "2");

        if (e.getSource() == b3)
            display.setText(display.getText() + "3");

        if (e.getSource() == b4)
            display.setText(display.getText() + "4");

        if (e.getSource() == b5)
            display.setText(display.getText() + "5");

        if (e.getSource() == b6)
            display.setText(display.getText() + "6");

        if (e.getSource() == b7)
            display.setText(display.getText() + "7");

        if (e.getSource() == b8)
            display.setText(display.getText() + "8");

        if (e.getSource() == b9)
            display.setText(display.getText() + "9");

        if (e.getSource() == b0)
            display.setText(display.getText() + "0");

        if (e.getSource() == add) {
            num1 = Double.parseDouble(display.getText());
            operator = '+';
            display.setText("");
        }

        if (e.getSource() == sub) {
            num1 = Double.parseDouble(display.getText());
            operator = '-';
            display.setText("");
        }

        if (e.getSource() == mul) {
            num1 = Double.parseDouble(display.getText());
            operator = '*';
            display.setText("");
        }

        if (e.getSource() == div) {
            num1 = Double.parseDouble(display.getText());
            operator = '/';
            display.setText("");
        }

        if (e.getSource() == equal) {

            num2 = Double.parseDouble(display.getText());

            if (operator == '+')
                result = num1 + num2;

            if (operator == '-')
                result = num1 - num2;

            if (operator == '*')
                result = num1 * num2;

            if (operator == '/')
                result = num1 / num2;

            display.setText(String.valueOf(result));
        }

        if (e.getSource() == clear) {
            display.setText("");
        }
    }

    public static void main(String[] args) {
        new Calculator();
    }
}