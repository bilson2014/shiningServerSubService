package com.dawn.net;

public abstract interface DownloadListener
{
  public abstract void onProgressChanged(String paramString, int paramInt);

  public abstract void onSucess(String paramString);

  public abstract void onError(String paramString);
}

