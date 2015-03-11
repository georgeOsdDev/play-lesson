package myapp.models

import myapp.db.DB

trait BaseModel {

  protected val ds = DB.ds

  protected val ID = "_id"
  protected val CREATED_AT = "created_at"
  protected val UPDATED_AT = "updated_at"
  protected val DELETED_AT = "deleted_at"

}
