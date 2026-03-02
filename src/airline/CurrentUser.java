/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package airline;

/**
 *
 * @author sidhi
 */

public class CurrentUser {
    private static int passengerId;

    public static void setPassengerId(int id) {
        passengerId = id;
    }

    public static int getPassengerId() {
        return passengerId;
    }
}
