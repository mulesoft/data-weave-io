package org.mule.weave.io.http.netty.cookie

import io.netty.handler.codec.http.cookie.Cookie
import io.netty.handler.codec.http.cookie.DefaultCookie
import org.asynchttpclient.cookie.CookieStore
import org.asynchttpclient.uri.Uri
import org.mule.weave.v2.module.http.netty.cookie.EmptyCookieStore
import org.scalatest.Assertion
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class EmptyCookieStoreTest extends AnyFreeSpec with Matchers {

  "EmptyCookieStore" - {
    val store = EmptyCookieStore()
    val uri = Uri.create("http://domain")
    val cookie = new DefaultCookie("c1", "c1Value")
    store.add(uri, cookie)

    "`add` call should not increase cookies count" in {
      val myUri = Uri.create("http://localhost")
      val myCookie = new DefaultCookie("c2", "c2Value")
      store.add(myUri, myCookie)

      assertIsEmptyFor(store, myUri)
      assertIsEmpty(store)
    }

    "`get` should return empty cookies for any uri" in {
      assertIsEmptyFor(store, uri)
    }

    "`getAll` should return empty cookies" in {
      assertIsEmpty(store)
    }

    "`remove` should return `false`" in {
      val removed = store.remove((_: Cookie) => {
        true
      })
      removed shouldBe false
    }

    "`clear` should return `false`" in {
      val cleared = store.clear()
      cleared shouldBe false
    }

    "`incrementAndGet` should return `0`" in {
      val count = store.incrementAndGet()
      count shouldBe 0
    }

    "`decrementAndGet` should return `0`" in {
      val count = store.decrementAndGet()
      count shouldBe 0
    }

    "`count` should return `0`" in {
      val count = store.count()
      count shouldBe 0
    }
  }

  private def assertIsEmptyFor(cookieStore: CookieStore, uri:Uri): Assertion = {
    val cookies = cookieStore.get(uri)
    cookies.isEmpty shouldBe true

    val all = cookieStore.getAll
    all.isEmpty shouldBe true
  }

  private def assertIsEmpty(cookieStore: CookieStore): Assertion = {
    val all = cookieStore.getAll
    all.isEmpty shouldBe true
  }
}
