package com.example.enterprise.session;

import java.io.PrintWriter;
import java.util.Scanner;

public class RootSession implements Session {
    @Override
    public void start(Scanner in, PrintWriter out) {
        out.println("--- logged in as the system administrator ---");
        boolean alive = true;
        while (alive) {
            try {
                /* 获取并检查命令 */
                out.println("input command: ");
                String message = in.nextLine();
                String[] parts = message.split("\\s+");
                if (parts.length == 0) {
                    out.println("syntax error");
                    continue;
                }

                /* 根据命令内容分配任务 */
                String command = parts[0];
                switch (command) {
                    // TODO: 增加一些命令

                    case "exit":
                        alive = false;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out.println("--- logged out ---");
    }
}
