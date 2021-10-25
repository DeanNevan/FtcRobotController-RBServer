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
    //创建回复消息和OpModeLog的builder
    RBMessage.Response.Builder responseBuilder = RBMessage.Response.newBuilder();
    RBMessage.RobotOpmodeLog.Builder robotOpmodeLogBuilder = RBMessage.RobotOpmodeLog.newBuilder();

    @Override
    public void runOpMode() {
        responseBuilder.setType(RBMessage.Type.ROBOT_OPMODE_LOG);//set the type of response 设置回复的类型
        robotOpmodeLogBuilder.setOpmodeName(this.getClass().getSimpleName());//设置OpMode的名称

        //向RBServer注册自己，用以向RBServer标识当前OpMode
        RBServer.getSingleton().registerOpMode(this);

        //设置Log的内容
        robotOpmodeLogBuilder.setContent("runOpMode init");
        responseBuilder.setRobotOpmodeLog(robotOpmodeLogBuilder.build());//set the RobotOpModeLog 设置RobotOpModeLog
        //调用RBServer的广播函数，发送response
        RBServer.getSingleton().broadcast(responseBuilder);

        //等待OpMode开始
        waitForStart();

        //设置Log的内容并调用RBServer的广播函数，发送response
        robotOpmodeLogBuilder.setContent("runOpMode start");
        responseBuilder.setRobotOpmodeLog(robotOpmodeLogBuilder.build());//set the RobotOpModeLog 设置RobotOpModeLog
        RBServer.getSingleton().broadcast(responseBuilder);

        while (opModeIsActive()) {

        }

        //设置Log的内容并调用RBServer的广播函数，发送response
        robotOpmodeLogBuilder.setContent("runOpMode stop");
        responseBuilder.setRobotOpmodeLog(robotOpmodeLogBuilder.build());//set the RobotOpModeLog 设置RobotOpModeLog
        RBServer.getSingleton().broadcast(responseBuilder);
    }

    @Override
    public void handleRBServerRobotRequest(Object msg) {
        RBMessage.Request request = (RBMessage.Request) msg;
        RBMessage.RequestRobotRequest robotRequest = request.getRequestRobotRequest();

        //code here to handle the request on the RBServer
        //在这里写代码，用于处理RBServer接收到的非连接/断开的请求
        //request是总体请求，robotRequest是request中细分的“机器人请求”

        String clientID = request.getClientID();//客户端id
        RBClient client = RBServerClientPool.getSingleton().getClientViaID(clientID);//可以这样子获取客户端对象
        int requestID = request.getRequestId();//请求id，前提是客户端有实现，不然都是0
        String request_content = robotRequest.getContent();//请求内容，string形式
        byte[] request_content_bytes = robotRequest.getBContent().toByteArray();//请求内容，byte[]形式

        Log.d("RB_BasicOpMode_Linear client_id:", request.getClientID());
        Log.d("RB_BasicOpMode_Linear request_id:", String.valueOf(requestID));
        Log.d("RB_BasicOpMode_Linear request_content:", request_content);

        //接下来是构建针对机器人请求的回复，这一段不是必须的
        RBMessage.ResponseRobotRequest.Builder responseRobotRequestBuilder = RBMessage.ResponseRobotRequest.newBuilder();
        responseRobotRequestBuilder
                .setResult(true);
        RBMessage.Response.Builder responseBuilder = RBMessage.Response.newBuilder();
        responseBuilder
                .setType(RBMessage.Type.ROBOT_REQUEST)
                .setRequestId(request.getRequestId())
                .setClientID(request.getClientID())
                .setResponseRobotRequest(responseRobotRequestBuilder.build());
        //回复请求方
        RBServer.getSingleton().sendToClient(request.getClientID(), responseBuilder);
    }
}
