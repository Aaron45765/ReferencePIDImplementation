public interface PIDOutput {
    //We need to create an interface that classes will implement, so that we know that every class will have this set() method.
    //We will pass the PIDOutput to the PIDController so the Controller knows where to set the outputs.
    //This is pretty much the same thing as whenever we say "extends Subsystem", except extending is only for subclasses.
    //In the case of an interface, we only need to write "public class Something implements PIDOutput".

    void set(double demand);
}
