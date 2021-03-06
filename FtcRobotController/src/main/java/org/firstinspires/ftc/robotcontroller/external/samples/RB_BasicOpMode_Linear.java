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

package org.firstinspires.ftc.robotcontroller.external.samples;

import android.util.Log;

import com.example.rbserver.client.RBClient;
import com.example.rbserver.pool.RBServerClientPool;
import com.example.rbserver.protobuf.RBMessage;
import com.example.rbserver.server.RBServer;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


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

@TeleOp(name="RB Basic: Linear OpMode", group="RB Linear Opmode")
@Disabled
public class RB_BasicOpMode_Linear extends RBLinearOpMode {
    //create the builder of response & OpModeLog
    //?????????????????????OpModeLog???builder
    RBMessage.Response.Builder responseBuilder = RBMessage.Response.newBuilder();
    RBMessage.RobotOpmodeLog.Builder robotOpmodeLogBuilder = RBMessage.RobotOpmodeLog.newBuilder();

    @Override
    public void runOpMode() {
        responseBuilder.setType(RBMessage.Type.ROBOT_OPMODE_LOG);//set the type of response ?????????????????????
        robotOpmodeLogBuilder.setOpmodeName(this.getClass().getSimpleName());//??????OpMode?????????

        //???RBServer????????????????????????RBServer????????????OpMode
        RBServer.getSingleton().registerOpMode(this);

        //??????
        opModeBroadcast("runOpMode init");

        //??????OpMode??????
        waitForStart();

        opModeBroadcast("runOpMode start");

        while (opModeIsActive()) {

        }

        opModeBroadcast("runOpMode stop");
    }

    public void opModeBroadcast(String content){
        //??????Log??????????????????RBServer????????????????????????response
        robotOpmodeLogBuilder.setContent(content);
        responseBuilder.setRobotOpmodeLog(robotOpmodeLogBuilder.build());//set the RobotOpModeLog ??????RobotOpModeLog
        RBServer.getSingleton().broadcast(responseBuilder);
    }

    @Override
    public void handleRBServerRobotRequest(Object msg) {
        RBMessage.Request request = (RBMessage.Request) msg;
        RBMessage.RequestRobotRequest robotRequest = request.getRequestRobotRequest();

        //code here to handle the request on the RBServer
        //?????????????????????????????????RBServer?????????????????????/???????????????
        //request??????????????????robotRequest???request?????????????????????????????????

        String clientID = request.getClientID();//?????????id
        RBClient client = RBServerClientPool.getSingleton().getClientViaID(clientID);//????????????????????????????????????
        int requestID = request.getRequestId();//??????id?????????????????????????????????????????????0
        String request_content = robotRequest.getContent();//???????????????string??????
        byte[] request_content_bytes = robotRequest.getBContent().toByteArray();//???????????????byte[]??????

        Log.d("RB_BasicOpMode_Linear client_id:", request.getClientID());
        Log.d("RB_BasicOpMode_Linear request_id:", String.valueOf(requestID));
        Log.d("RB_BasicOpMode_Linear request_content:", request_content);

        //???????????????????????????????????????????????????????????????????????????
        RBMessage.ResponseRobotRequest.Builder responseRobotRequestBuilder = RBMessage.ResponseRobotRequest.newBuilder();
        responseRobotRequestBuilder
                .setResult(true);
        RBMessage.Response.Builder responseBuilder = RBMessage.Response.newBuilder();
        responseBuilder
                .setType(RBMessage.Type.ROBOT_REQUEST)
                .setRequestId(request.getRequestId())
                .setClientID(request.getClientID())
                .setResponseRobotRequest(responseRobotRequestBuilder.build());
        //???????????????
        RBServer.getSingleton().sendToClient(request.getClientID(), responseBuilder);
    }
}
