import java.util.Scanner;

class Stack<T>
{
    Object a[] = new Object[100];
    int top;

    Stack()
    {
        top = -1;
    }

    void push(T x)
    {
        if(top == 99)
            System.out.println("Stack Overflow");
        else
            a[++top] = x;
    }

    void pop()
    {
        if(top == -1)
            System.out.println("Stack Underflow");
        else
            System.out.println("Deleted: " + a[top--]);
    }

    void display()
    {
        if(top == -1)
            System.out.println("Stack is Empty");
        else
        {
            for(int i = top; i >= 0; i--)
                System.out.print(a[i] + " ");

            System.out.println();
        }
    }

    T peek()
    {
        return (T)a[top];
    }

    boolean empty()
    {
        return top == -1;
    }
}

class Main
{
    static int priority(char ch)
    {
        if(ch == '^')
            return 3;
        if(ch == '*' || ch == '/')
            return 2;
        if(ch == '+' || ch == '-')
            return 1;

        return 0;
    }

    static String infixToPostfix(String exp)
    {
        Stack<Character> s = new Stack<Character>();
        String postfix = "";

        for(int i = 0; i < exp.length(); i++)
        {
            char ch = exp.charAt(i);

            if(Character.isLetterOrDigit(ch))
                postfix += ch;

            else if(ch == '(')
                s.push(ch);

            else if(ch == ')').
            {
                while(!s.empty() && s.peek() != '(')
                {
                    postfix += s.peek();
                    s.pop();
                }

                if(!s.empty())
                    s.pop();
            }

            else
            {
                while(!s.empty() && s.peek() != '(' &&
                      (priority(s.peek()) > priority(ch) ||
                      (priority(s.peek()) == priority(ch) && ch != '^')))
                {
                    postfix += s.peek();
                    s.pop();
                }

                s.push(ch);
            }
        }

        while(!s.empty())
        {
            postfix += s.peek();
            s.pop();
        }

        return postfix;
    }

    public static void main(String args[])
    {
        Scanner sc = new Scanner(System.in);

        Stack<Integer> s = new Stack<Integer>();

        int ch, x;

        do
        {
            System.out.println("\n1.Push");
            System.out.println("2.Pop");
            System.out.println("3.Display");
            System.out.println("4.Infix to Postfix");
            System.out.println("5.Exit");

            System.out.print("Enter choice: ");
            ch = sc.nextInt();

            switch(ch)
            {
                case 1:
                    System.out.print("Enter element: ");
                    x = sc.nextInt();
                    s.push(x);
                    break;

                case 2:
                    s.pop();
                    break;

                case 3:
                    s.display();
                    break;

                case 4:
                    System.out.print("Enter infix expression: ");
                    String exp = sc.next();

                    System.out.println("Postfix expression: " +
                                       infixToPostfix(exp));
                    break;

                case 5:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice");
            }

        } while(ch != 5);

        sc.close();
    }
}