package com.lb12.topimobiledevelopertest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import static com.lb12.topimobiledevelopertest.IngredientsModel.populateIngredientList;
import static com.lb12.topimobiledevelopertest.MealsViewModel.callMealListFromPHP;

public class MealsAdapter {

    public static MealsModel.Meal meal;

    public static class Adapter extends RecyclerView.Adapter< Adapter.MyViewHolder >{

        private final Context context;

        private List<MealsModel.Meal> mealsList;
        private RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);

        public Adapter(Context context){
            this.context = context;
        }

        public void updateList(List<MealsModel.Meal> mealsList){
            this.mealsList = mealsList;
        }

        @NonNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = LayoutInflater.from( parent.getContext() ).
                    inflate(
                            R.layout.adaptermeals,
                            parent,
                            false
                    );
            return new MyViewHolder( item );
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MealsModel.Meal meal = mealsList.get( position );
            holder.strMeal.setText( meal.getStrMeal() );
            holder.strArea.setText( meal.getStrArea() );
            holder.strCategory.setText( meal.getStrCategory() );
            Glide.with(context).
                    load(meal.getStrMealThumb()).
                    centerCrop().
                    apply(requestOptions).
                    into(holder.strMealThumb);
        }

        @Override
        public int getItemCount() {
            return mealsList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            TextView strMeal;
            TextView strArea;
            TextView strCategory;
            ImageView strMealThumb;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                strMeal      = itemView.findViewById( R.id.strMeal      );
                strArea      = itemView.findViewById( R.id.strArea      );
                strCategory  = itemView.findViewById( R.id.strCategory  );
                strMealThumb = itemView.findViewById( R.id.strMealThumb );
            }
        }

        public void clear() {
            mealsList.clear();
            notifyDataSetChanged();
        }

    }

    static void createRecyclerViewClick(
            RecyclerView recyclerView,
            Context appContext,
            List<MealsModel.Meal> mealList
    ){

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        appContext,
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                MealsModel.Meal meal = mealList.get( position );
                                ArrayList<String> ingredientsList = new ArrayList<String>();

                                populateIngredientList(
                                        ingredientsList,
                                        meal
                                );

                                Intent intent =  new Intent(
                                        view.getContext(),
                                        IngredientsActivity.class
                                );
                                Bundle bundle = new Bundle();

                                bundle.putString( "StrMealThumb", meal.getStrMealThumb() );
                                bundle.putString( "StrInstructions", meal.getStrInstructions() );
                                bundle.putStringArrayList( "ingredientList", ingredientsList );

                                intent.putExtras( bundle );

                                view.getContext().startActivity(intent);

                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                                MealsModel.Meal meal = mealList.get( position );
                                Toast.makeText(
                                        appContext,
                                        "Meal Code: " + meal.getIdMeal(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        }
                )
        );

    }

    static void createRecyclerViewSwipe(
            SwipeRefreshLayout swipeContainer,
            MealsAdapter.Adapter adapter,
            Context appContext,
            ProgressDialog progressDialog
    ){

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                callMealListFromPHP(
                        swipeContainer,
                        appContext,
                        progressDialog,
                        adapter
                );
            }
        });

    }

    public static MealsModel.Meal getMeal() {
        return meal;
    }

    void setMeal(MealsModel.Meal meal) {
        this.meal = meal;
    }


}
