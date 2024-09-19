package com.freefish.torchesbecomesunlight.server.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

public class Parabola {
    double a;double b;double c;double x;double z;double x2;

    public void mathParabola(Entity patriot, LivingEntity target){
        double x1 = 0.001;
        double y1 = 0.001;
        this.x = target.getX() - patriot.getX();
        this.z = target.getZ() - patriot.getZ();
        this.x2 = Math.sqrt(x*x+z*z);
        double y2 = target.getY() - patriot.getY();
        double x3 = x2/2;
        double y3 = 60;
        double[][] matrix = {
                {x1 * x1, x1, 1},
                {x2 * x2, x2, 1},
                {x3 * x3, x3, 1}
        };
        double[] constants = {y1, y2, y3};

        double[] solution = solveEquation(matrix, constants);
        a = solution[0];
        b = solution[1];
        c = solution[2];
    }

    public void mathParabola(Entity patriot, Vector3f targetPos){
        double x1 = 0.001;
        double y1 = 0.001;
        this.x = targetPos.x - patriot.getX();
        this.z = targetPos.z - patriot.getZ();
        this.x2 = Math.sqrt(x*x+z*z);
        double y2 = targetPos.y - patriot.getY();
        double x3 = x2/2;
        double y3 = 60;
        double[][] matrix = {
                {x1 * x1, x1, 1},
                {x2 * x2, x2, 1},
                {x3 * x3, x3, 1}
        };
        double[] constants = {y1, y2, y3};

        double[] solution = solveEquation(matrix, constants);
        a = solution[0];
        b = solution[1];
        c = solution[2];
    }

    private static double[] solveEquation(double[][] matrix, double[] constants) {
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            double pivot = matrix[i][i];
            for (int j = i + 1; j < n; j++) {
                double ratio = matrix[j][i] / pivot;
                for (int k = i; k < n; k++) {
                    matrix[j][k] -= ratio * matrix[i][k];
                }
                constants[j] -= ratio * constants[i];
            }
        }
        double[] solution = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += matrix[i][j] * solution[j];
            }
            solution[i] = (constants[i] - sum) / matrix[i][i];
        }
        return solution;
    }

    public double getY(double x3) {
        return a*x3*x3+b*x3+c;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getX() {
        return x;
    }

    public double getX2() {
        return x2;
    }

    public double getZ() {
        return z;
    }
}
