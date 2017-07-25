package com.graffitiboard.graffiti.path.manager;

import com.graffitiboard.graffiti.path.CachePath;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/23.
 */

public class PathManager {

    public static final int MAX_PATH_SIZE = 20;

    private List<CachePath> mDrawList;

    private List<CachePath> mRemoveList;

    public PathManager(){
        mDrawList = new ArrayList<>(MAX_PATH_SIZE);
        mRemoveList = new ArrayList<>(MAX_PATH_SIZE);
    }

    public void addPath(CachePath cache){
        if(mDrawList.size() == MAX_PATH_SIZE){
           mDrawList.remove(0);
        }
        mDrawList.add(cache);
    }

    /**
     * 恢复
     */
    public void recovery(){
        int size = mRemoveList.size();
        if (size > 0){
            CachePath cache = mRemoveList.remove(size-1);
            mDrawList.add(cache);
        }
    }

    /**
     * 撤销
     */
    public void revoke(){
        int size = mDrawList.size();
        if (size > 0){
            CachePath cache = mDrawList.remove(size-1);
            mRemoveList.add(cache);
        }
    }

    public List<CachePath> getDrawList(){
        return mDrawList;
    }

    public List<CachePath> getRemoveList(){
        return mRemoveList;
    }

    public void clearPath(){
        if (mDrawList != null){
            mDrawList.clear();
        }
        if (mRemoveList != null){
            mRemoveList.clear();
        }
    }
}
