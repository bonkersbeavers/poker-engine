package server.grpc.converters

import com.google.gson.Gson
import poker.proto.TableSettings
import server.grpc.CashGameTableSettings

fun TableSettings.toCashGameTableSettings(): CashGameTableSettings {
    return Gson().fromJson(this.jsonSettings, CashGameTableSettings::class.java)
}
