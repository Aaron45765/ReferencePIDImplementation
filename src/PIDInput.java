public interface PIDInput {
    //We need to create an interface that classes will implement, so that we know that every class will have this get() method.
    //We will pass the PIDInput to the PIDController so the Controller knows where the sensor position is.
    //In the case of an interface, we only need to write "public class Something implements PIDInput".
    double get();
}
