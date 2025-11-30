package org.firstinspires.ftc.teamcode.subsystems.enums;

import androidx.annotation.NonNull;

/**
 * This is a template for creating enums. They enumerate
 * magic numbers into friendly names and are quite useful
 * for repetitive features like buttons or offsets.
 * @author Mentor Landon Smith
 */
public enum EnumTemplate {
    /*
     * Use typical naming conventions for the name of the variable
     * in code, but give it a friendly name for identification in
     * exceptions and telemetry. As defined in the parenthesis,
     * when an enum is initialized, the friendly name is the default
     * string to pass through to the constructor.
     */
    VALUE_1("Friendly Name 1"),
    VALUE_2("Friendly Name 2");
    /*
     * This string is what stores the friendly name of
     * the enum value initialized.
     */
    private final String displayName;
    /*
     * I haven't seen enums typically use constructors for more
     * than just this, but it is a constructor nonetheless.
     */
    EnumTemplate(String displayName) {
        this.displayName = displayName;
    }
    /*
     * Every Java object inherits Object, the parent class
     * of all objects. In this instance, the Object class
     * has it's own toString() method that this class inherits,
     * but we want different behavior. This override defines that
     * behavior, and is annotated with the @Override annotation
     * so that if anything goes wrong, the code will error out
     * before compilation completes.
     */
    @NonNull
    @Override
    public String toString(){return displayName;}
}
