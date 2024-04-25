package com.example.fooddeliveryapp.ApplicationUtilities;

import javax.annotation.Nullable;

public class FormValidator
{

    static String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,} ?$";
    static String passwordRegex = "(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@_$!%*?&])[A-Za-z\\d@_$!%*?&]{length,}";

    static String nameRegex = "[a-zA-Z0-9 ]+";
    public static Object emailValidator(String email, @Nullable String emptyMessage, @Nullable String errorMessage)
    {
        if( !email.isEmpty() )
        {
            if( email.matches(emailRegex) )
                return true;
            if( errorMessage != null )
                return errorMessage;
            return "Email provided is invalid. (example@example.com)";
        }
        if( emptyMessage != null )
            return emptyMessage;
        return "Email field should not be empty.";
    }

    public static Object passwordValidator( String password, int passwordLength, @Nullable String emptyMessage )
    {
        String errorMessage = "Password must contain uppercases, lowercases, digits, and special characters.";
        passwordRegex = passwordRegex.replaceAll("length", passwordLength+"");
        if( !password.isEmpty() )
        {
            if( password.length() > passwordLength )
            {
                if ( password.matches(passwordRegex) )
                    return true;

                return errorMessage;
            }
            else {
                return "Password length should be minimum "+ passwordLength +" characters.";
            }
        }
        else
        {
            if( emptyMessage != null )
                return emptyMessage;
            return "Password field should not be empty.";
        }
    }

    public static Object nameValidator( String name, String emptyMessage, String errorMessage)
    {
        if( !name.isEmpty() )
        {
            if( name.matches(nameRegex) )
                return true;
            return "Name can only contain letters and numbers.";
        }
        return "Name field should not be empty.";
    }
}
