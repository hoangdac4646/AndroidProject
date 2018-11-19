package com.example.anh.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anh.myapplication.R;
import com.example.anh.myapplication.fragments.CellGroupFragment;
import com.example.anh.myapplication.model.Board;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CellGroupFragment.OnFragmentInteractionListener {
    private TextView clickedCell;
    private int clickedGroup;
    private int clickedCellId;
    private Board startBoard;
    private Board currentBoard;
    private int IndexFile;
    private int FileID;

    public void RandomIndexFileAndFileID(){
        Random rand = new Random();
        IndexFile = rand.nextInt(3) + 1;
        if(IndexFile == 1){
            FileID = R.raw.b1;
        }else if(IndexFile == 2){
            FileID = R.raw.b2;
        }else if(IndexFile == 3){
            FileID = R.raw.b3;
        }
    }


    private ArrayList<Board> readFileMapSudoku() {
        ArrayList<Board> boards = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(FileID);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                Board board = new Board();
                for (int i = 0; i < 9; i++) {
                    String rowCells[] = line.split(" ");
                    for (int j = 0; j < 9; j++) {
                        if (rowCells[j].equals("-")) {
                            board.setValue(i, j, 0);
                        } else {
                            board.setValue(i, j, Integer.parseInt(rowCells[j]));
                        }
                    }
                    line = bufferedReader.readLine();
                }
                boards.add(board);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            Toast.makeText(this, "Không Thể Đọc FILE", Toast.LENGTH_SHORT).show();
        }
        return boards;
    }

    private Board chooseRandomBoard(ArrayList<Board> boards) {
        int randomNumber = (int) (Math.random() * boards.size());
        return boards.get(randomNumber);
    }

    private boolean isStartPiece(int group, int cell) {
        int row = ((group-1)/3)*3 + (cell/3);
        int column = ((group-1)%3)*3 + ((cell)%3);
        return startBoard.getValue(row, column) != 0;
    }

    @Override
    public void onFragmentInteraction(int groupId, int cellId, View view) {
        clickedCell = (TextView) view;
        clickedGroup = groupId;
        clickedCellId = cellId;
        if (!isStartPiece(groupId, cellId)) {
            if(TextUtils.isEmpty(clickedCell.getText())){
                clickedCell.setText("1");
            }else{
                int value = Integer.parseInt(clickedCell.getText().toString()) + 1;
                if(value == 10){
                    value = 1;
                }
                clickedCell.setText(value + "");
            }
        } else {
            Toast.makeText(this, "Không Thể Thay Đổi", Toast.LENGTH_SHORT).show();
        }
    }

    public void VeMapSudoku(){
        ArrayList<Board> boards = readFileMapSudoku();
        startBoard = chooseRandomBoard(boards);
        currentBoard = new Board();
        currentBoard.copyValues(startBoard.getGameCells());

        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 1; i < 10; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i-1]);
            thisCellGroupFragment.setGroupId(i);
        }

        CellGroupFragment tempCellGroupFragment;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int column = j / 3;
                int row = i / 3;

                int fragmentNumber = (row * 3) + column;
                tempCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber]);
                int groupColumn = j % 3;
                int groupRow = i % 3;

                int groupPosition = (groupRow * 3) + groupColumn;
                int currentValue = currentBoard.getValue(i, j);

                if (currentValue != 0) {
                    tempCellGroupFragment.setValue(groupPosition, currentValue);
                }
            }
        }
    }

    private boolean KiemTraDungTheoGroup() {
        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 0; i < 9; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i]);
            if (!thisCellGroupFragment.checkGroupCorrect()) {
                return false;
            }
        }
        return true;
    }

    //Kiem Tra Sudoku Đúng
    public void onKiemTraDung(View view) {
        currentBoard.isBoardCorrect();
        try {
            if(KiemTraDungTheoGroup() && currentBoard.isBoardCorrect()) {
                Toast.makeText(this, "Đúng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sai", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Bạn Chưa Hoàn Thành Mà", Toast.LENGTH_SHORT).show();
        }

    }

    public void onLayDapAn(View view){
        if(IndexFile == 1){
            FileID = R.raw.g1;
        }else if(IndexFile == 2){
            FileID = R.raw.g2;
        }else if(IndexFile == 3){
            FileID = R.raw.g3;
        }
        VeMapSudoku();
    }

    //=========================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RandomIndexFileAndFileID();
        VeMapSudoku();
    }
}
