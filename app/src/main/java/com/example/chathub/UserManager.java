package com.example.chathub;


public class UserManager
{

    public static void init()
    {

    }

    public static void rememberMe(boolean remember)
    {
        // rememberMe = remember;
    }
    public static boolean isLoggedIn()
    {
        return false;
    }

    // get user
    public static String getUser()
    {
        return null;
    }

    public static void register(String email, String password)
    {
//        mAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(task ->
//            {
//                if (task.isSuccessful())
//                {
//                    // Sign in success, update UI with the signed-in user's information
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    System.out.println("Registration successful for user: " + user.getEmail());
//                }
//                else
//                {
//                    // If sign in fails, display a message to the user.
//                    System.out.println("Registration failed.");
//                }
//            });
    }

    public static void login(String email, String password)
    {
//        mAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(task -> {
//                if (task.isSuccessful())
//                {
//                    // Sign in success, update UI with the signed-in user's information
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    System.out.println("Login successful for user: " + user.getEmail());
//                }
//                else
//                {
//                    // If sign in fails, display a message to the user.
//                    System.out.println("Login failed.");
//                }
//            });
    }
}