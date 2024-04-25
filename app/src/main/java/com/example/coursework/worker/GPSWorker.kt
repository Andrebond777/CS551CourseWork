package com.example.coursework.worker

import android.Manifest
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.workDataOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "GPSWorker"
private const val GPS_WORKER_OUTPUT_TAG = "GPS"

