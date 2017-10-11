package com.appsys.bakingapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.appsys.bakingapp.fragment.IngredientsFragment;
import com.appsys.bakingapp.fragment.StepFragment;
import com.appsys.bakingapp.fragment.StepsFragment;
import com.appsys.bakingapp.modal.Ingredient;
import com.appsys.bakingapp.modal.Recipe;
import com.appsys.bakingapp.modal.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements StepsFragment.StepsCallback {

    private Recipe mRecipe;
    private Ingredient mIngredient;
    private Step mStep;
    private Toast mToast;
    private boolean mPhone = true;
    private int mCurrentIndex = -2;
    private static String STACK_RECIPE_DETAIL="STACK_RECIPE_DETAIL";
    private static String STACK_RECIPE_STEP_DETAIL="STACK_RECIPE_STEP_DETAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        FragmentManager fm = getFragmentManager();
        mPhone = (findViewById(R.id.detail_list_fragment) == null);

        if (savedInstanceState == null) {
            Intent data = getIntent();
            if (data != null && data.hasExtra("recipe")) {
                mRecipe = data.getParcelableExtra("recipe");
            }
        } else if (savedInstanceState.containsKey("recipe")) {
            mRecipe = savedInstanceState.getParcelable("recipe");
            mCurrentIndex = savedInstanceState.getInt("currentIndex", -2);
        }

        if (mRecipe == null) {
            showMessage("please contact to developer");
            finish();
            return;
        }

        if (savedInstanceState == null) {
            StepsFragment stepsFragment = StepsFragment.newInstance(mRecipe);
            fm = getFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.master_list_fragment, stepsFragment).addToBackStack(STACK_RECIPE_DETAIL)
                    .commit();
        }

        ButterKnife.bind(this);

        if (!mPhone) {
            if (fm.getBackStackEntryCount() > 1) {
                fm.popBackStack(STACK_RECIPE_DETAIL, 0);
            }
            if (mCurrentIndex < 0) {
                IngredientsFragment ingredientsFragment = IngredientsFragment.newInstance(mRecipe.getIngredients());
                fm.beginTransaction()
                        .replace(R.id.detail_list_fragment, ingredientsFragment)
                        .commit();
            } else {
                StepFragment stepFragment = StepFragment.newInstance(mRecipe.getSteps(), mCurrentIndex);
                FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.detail_list_fragment, stepFragment);
                ft.commit();
            }
        } else {
            if (mCurrentIndex == -1) {
                IngredientsFragment ingredientsFragment = IngredientsFragment.newInstance(mRecipe.getIngredients());
                fm.beginTransaction()
                        .replace(R.id.master_list_fragment, ingredientsFragment).addToBackStack(STACK_RECIPE_STEP_DETAIL)
                        .commit();
            } else if (mCurrentIndex > -1) {
                StepFragment stepFragment = StepFragment.newInstance(mRecipe.getSteps(), mCurrentIndex);
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.master_list_fragment, stepFragment).addToBackStack(STACK_RECIPE_STEP_DETAIL);
                ft.commit();
            }
        }


    }

    private void showMessage(String msg) {
        if (mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(DetailActivity.this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mPhone) {
            FragmentManager fm = getFragmentManager();
            if (fm.getBackStackEntryCount() > 1) {
                fm.popBackStack(STACK_RECIPE_DETAIL, 0);
                mCurrentIndex = -2;
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onIngredientClick(List<Ingredient> list) {
        mCurrentIndex = -1;
        FragmentManager fm = getFragmentManager();
        IngredientsFragment ingredientsFragment = IngredientsFragment.newInstance(mRecipe.getIngredients());
        FragmentTransaction ft = fm.beginTransaction();
        if (mPhone) {
            ft.replace(R.id.master_list_fragment, ingredientsFragment).addToBackStack(STACK_RECIPE_STEP_DETAIL);
        } else {
            ft.replace(R.id.detail_list_fragment, ingredientsFragment);
        }
        ft.commit();
    }

    @Override
    public void onStepClick(List<Step> list, int position) {
        mCurrentIndex = position;
        FragmentManager fm = getFragmentManager();
        StepFragment stepsFragment = StepFragment.newInstance(mRecipe.getSteps(), position);
        FragmentTransaction ft = fm.beginTransaction();
        if (mPhone) {
            ft.replace(R.id.master_list_fragment, stepsFragment).addToBackStack(STACK_RECIPE_STEP_DETAIL);
        } else {
            ft.replace(R.id.detail_list_fragment, stepsFragment);
        }
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("recipe", mRecipe);
        outState.putInt("currentIndex", mCurrentIndex);
        super.onSaveInstanceState(outState);
    }
}
