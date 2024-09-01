package org.firstinspires.ftc.teamcode.archive;
import org.firstinspires.ftc.robotcore.external.Telemetry;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;


//Do not use, not ready--------------------------------------------------------------------------------------------------------------------
public class TelemetryManager {

    Map<Integer, List<String>> telemetryData = new TreeMap<Integer, List<String>>();

    Telemetry telemetryFTC; //inherit the telemetry
    public TelemetryManager(Telemetry telemetry) throws Exception{
        this.telemetryFTC = telemetry;
        throw new Exception("This is not ready, do not use");
    }

    public void upDate(int[] telemetryTypes){
        for (int iTypes = 0; iTypes <= telemetryTypes.length; iTypes++){

            for (int i = 0; i <= telemetryData.get(iTypes).size(); i++) {
                telemetryFTC.addLine(telemetryData.get(iTypes).get(i));
            }
        }
        telemetryFTC.update();
    }


    public void addData(String data){
    }

}
