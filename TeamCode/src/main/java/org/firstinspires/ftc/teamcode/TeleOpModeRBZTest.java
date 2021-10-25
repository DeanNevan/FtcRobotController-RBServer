/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.example.rbserver.protobuf.RBMessage;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.RBLinearOpMode;

import java.text.DecimalFormat;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="TeleOpModeRBZTest", group="Linear Opmode")
//@Disabled
public class TeleOpModeRBZTest extends RBLinearOpMode {
    private DcMotor motorTest1 = null;
    private DcMotor motorTest2 = null;
    private DcMotor motorTest3 = null;
    private DcMotor motorTest4 = null;

    private double ratioTest1 = 25.9;
    private double ratioTest2 = 25.9;
    private double ratioTest3 = 25.9;
    private double ratioTest4 = 25.9;

    private double lastValueTest1 = 0;
    private double lastValueTest2 = 0;
    private double lastValueTest3 = 0;
    private double lastValueTest4 = 0;

    Vector2 towards = new Vector2(0, 0);

    double RATIO_GEERBOX = 32.0 / 13;

    private double lastTime = 0;
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode() {
        motorTest1 = hardwareMap.get(DcMotor.class, "motor_test_1");
        motorTest1.setDirection(DcMotorSimple.Direction.REVERSE);
        motorTest2 = hardwareMap.get(DcMotor.class, "motor_test_2");
        motorTest3 = hardwareMap.get(DcMotor.class, "motor_test_3");
        motorTest4 = hardwareMap.get(DcMotor.class, "motor_test_4");
        telemetry.addLine("初始化完毕");

        telemetry.addLine(
                "---测试四个电机---\n" +
                        "一号手柄\n" +
                        "左摇杆x、y\n" +
                        "右摇杆x、y\n" +
                        "分别对应\n" +
                        "test1、test2、test3、test4"
        );
        telemetry.update();
        waitForStart();
        timer.reset();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double x = gamepad1.left_stick_x;
            double y = gamepad1.left_stick_y;

            motorTest1.setPower(y+x);
            motorTest2.setPower(-y+x);

            double deltaValue1 = motorTest1.getCurrentPosition() - lastValueTest1;
            double deltaValue2 = motorTest2.getCurrentPosition() - lastValueTest2;
            double deltaValue3 = motorTest3.getCurrentPosition() - lastValueTest3;
            double deltaValue4 = motorTest4.getCurrentPosition() - lastValueTest4;

            double deltaTime = timer.seconds() - lastTime;

            lastTime = timer.seconds();
            lastValueTest1 = motorTest1.getCurrentPosition();
            lastValueTest2 = motorTest2.getCurrentPosition();
            lastValueTest3 = motorTest3.getCurrentPosition();
            lastValueTest4 = motorTest4.getCurrentPosition();

            telemetry.addLine("---朝向---");
            telemetry.addData("x", towards.getX());
            telemetry.addData("y", towards.getY());
            telemetry.addLine("---编码器数值---");
            telemetry.addData("test1:", motorTest1.getCurrentPosition());
            telemetry.addData("test2:", motorTest2.getCurrentPosition());
            telemetry.addData("test3:", motorTest3.getCurrentPosition());
            telemetry.addData("test4:", motorTest4.getCurrentPosition());
            telemetry.addLine("---转速---");
            DecimalFormat df = new DecimalFormat("#.00");
            telemetry.addLine(String.format("test1:%s转/秒", df.format(deltaValue1 / ratioTest1 / deltaTime)));
            telemetry.addLine(String.format("test2:%s转/秒", df.format(deltaValue2 / ratioTest2 / deltaTime)));
            telemetry.addLine(String.format("test3:%s转/秒", df.format(deltaValue3 / ratioTest3 / deltaTime)));
            telemetry.addLine(String.format("test4:%s转/秒", df.format(deltaValue4 / ratioTest4 / deltaTime)));
            telemetry.update();
        }
    }

    @Override
    public void handleRBServerRobotRequest(Object msg) {
        RBMessage.Request request = (RBMessage.Request) msg;
        RBMessage.RequestRobotRequest robotRequest = request.getRequestRobotRequest();
        //code here to handle the request on the RBServer
        //you can find the request content in "robotRequest.getContent()"
        //在这里写代码，用于处理RBServer接收到的非连接/断开的请求
        //request是总体请求，robotRequest是request中细分的“机器人请求”
        //你可以在"robotRequest.getContent()"中获取请求内容，当然如果是bytes形式的话就在"robotRequest.getBContent()"
        Log.d("RB_BasicOpMode_Linear client_id:", request.getClientID());
        Log.d("RB_BasicOpMode_Linear request_id:", String.valueOf(request.getRequestId()));
        Log.d("RB_BasicOpMode_Linear type:", request.getType().name());
        Log.d("RB_BasicOpMode_Linear robot_request_message:", robotRequest.getContent());
    }
}
