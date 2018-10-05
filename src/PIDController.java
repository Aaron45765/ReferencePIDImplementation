public class PIDController {
    //This is a reference PID controller implementing the formula output = kP * e(t) + kI * int(e(t)) + kD e'(t)
    //Note that this is a self-contained PIDController with its own loop. This could easily be adapted to work in a command.

    private PIDInput input;     //declare variables
    private PIDOutput output;
    private int frequency;
    private double kP, kI, kD, kDt;
    private Thread PIDThread;                       //a thread is a place where our code can run independently of the code that's already happening.
    private volatile double setpoint, lastError,
                            currentError, sumError; // we use a thread because we don't want our PIDController to be affected by things that are going on in the robot thread.
    public PIDController(PIDInput input, PIDOutput output, double kP, double kI, double kD){ //We need a sensor input, something to output to, as well as our control constants.
        this.input = input;
        this.output = output;
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;

        setpoint = input.get();     //we're just getting the current position of the sensor and setting it to the setpoint, since we don't want the mechanism moving when we don't give it anything.
        lastError = getError();     //since we're first just constructing the object, the lastError is just the currentError
        currentError = lastError;   //this is the constructor so we just set them equal to each other.
        sumError = 0;               //this is the (sorta) integral of e(t) -- we're cheating a bit!
        frequency = 50;             //hertz, how fast we run the loop. faster isn't always == better, sometimes need to balance CPU load.

        PIDThread = new Thread(() -> {              //we're making a new thread here. a thread runs concurrently to the main thread.
            while(!PIDThread.isInterrupted()) {     //make this code loop forever until it's interrupted.
                if(System.nanoTime()/1E9 % (1/(double)frequency) == 0) {    //make the looping obey the frequency that we set.
                    lastError = currentError;       //at the beginning of a new loop we're setting the lastError to the old "currentError"
                    currentError = getError();      //and then we're updating the currentError.
                    sumError += getError();         //we sum the CURRENT value of the error to the sum of the error. this is kinda like the integral, but not really.
                    double changeInError = lastError - currentError;    //this is the change in the error, which is our e'(t) in the expression
                    double pidOutput = kP * getError() + kI * sumError + kD * changeInError;    //output = kP * e(t) + kI * int(e(t)) + kD e'(t) - learn your calculus kids
                    output.set(pidOutput);          //set the output to the calculated value.
                }
            }
        });
    }

    public void set(double setpoint){
        this.setpoint = setpoint;
    }

    public double getError(){
        return setpoint - input.get();
    }

    public void start(){
        PIDThread.start();
    }

    public void stop(){
        PIDThread.interrupt();
    }

}
