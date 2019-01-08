# AdaptiveRecyclerView

[![GitHub release](https://img.shields.io/github/release/pokk/AdaptiveRecyclerView.svg?style=flat-square)](https://github.com/pokk/AdaptiveRecyclerView)
[![license](https://img.shields.io/github/license/pokk/AdaptiveRecyclerView.svg?style=flat-square)](https://github.com/pokk/AdaptiveRecyclerView)

In the beginning, my purpose is for the expanding recycler view. For it, I need to make recycler
view adapt to each of the type of the list children. For this reason, first I open this adaptive
recycler view.

This adaptive recycler view might be not the easiest way to achieve. I'm using the concept of [Visitor
Pattern](http://www.wikiwand.com/en/Visitor_pattern) into this adaptive recycler view. Here there're
some roles I have to introduce first.

- **_Visitor_**: `AdaptiveAdapter`
- **_Visitable (Element)_**: `IVisitable`

We use them to get each of the type of the elements.

# How to use

#### First create concrete class

1. **RecyclerView.Adaptor**
2. **ViewHolder**
3. **Data Model Elements**
4. **ViewTypeFactory**

#### 🍷 Example

Make two type of the element class.

```kotlin
data class ProductTypeOne(var name: String,
                          override var childItemList: List<IExpandVisitor> = emptyList(),
                          override var isExpandable: Boolean = false): IVisitable {
    override fun type(typeFactory: MultiTypeFactory): Int = typeFactory.type(this)
}

data class ProductTypeTwo(var name: String,
                          override var childItemList: List<IExpandVisitor> = emptyList(),
                          override var isExpandable: Boolean = false): IVisitable {
    override fun type(typeFactory: MultiTypeFactory): Int = typeFactory.type(this)
}
```

Create a multiple view type factory for providing difference view type and view holder.

```kotlin
class MultiTypeFactory: ViewTypeFactory() {
 override var transformMap: MutableMap<Int, Pair<Int, (View) -> ViewHolder>> =
     mutableMapOf(1 to Pair(R.layout.item_first ) { itemView -> FirstViewHolder(itemView) },
         2 to Pair(R.layout.item_second ) { itemView -> SecondViewHolder(itemView) })

 fun type(typeone: ProductTypeOne): Int = 1

 fun type(typetwo: ProductTypeTwo): Int = 2
}
```

Depend on the difference layouts, we init in each of view holder. The view holder is like an
`activity`/`fragment`. I prefer to initial and control the components in the view holder.

```kotlin
class TypeOneViewHolder(view: View): AdaptiveViewHolder<MultiTypeFactory, ProductTypeOne>(view) {
    override fun initView(model: ProductTypeOne, position: Int, adapter: Any) {
        adapter as YouAdapterClassName
    }
}

class TypeTwoViewHolder(view: View): AdaptiveViewHolder<MultiTypeFactory, ProductTypeTwo>(view) {
    override fun initView(model: ProductTypeTwo, position: Int, adapter: Any) {
        adapter as YouAdapterClassName
    }
}
```

As like original using, it's so easy!

```kotlin
class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        val itemList: MutableList<IVisitable> =
            mutableListOf(ProductTypeTwo("iPhone 6 plus"),
                ProductTypeOne("Google"),
                ProductTypeOne("HTC"),
                ProductTypeTwo("iPhone 6"),
                ProductTypeTwo("iPhone 6s"),
                ProductTypeTwo("iPhone 7"),
                ProductTypeTwo("iPhone 6s"),
                ProductTypeOne("Mi"),
                ProductTypeOne("Facebook"))

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = AdaptiveAdapter(itemList)
    }
}
```

If you'd like to know more detail or how to implement the expanding recycler view, check the sample project
please. There is a simple sample for this library. 😄

## Gradle

It's very easy to import, you just put them into your gradle file.

```gradle
compile "com.devrapid.jieyi:adaptiverecyclerview:1.0.6"
```

## Maven

```maven
<dependency>
  <groupId>com.devrapid.jieyi</groupId>
  <artifactId>adaptiverecyclerview</artifactId>
  <version>1.0.5</version>
  <type>pom</type>
</dependency>
```

# Feature

- [ ] Reduce the inherit classe.
- [ ] The `adapter` parameter in the `AdaptiveViewHolder` to specific data type.

If you have any idea about making this library better, please give me an issue.
Thank you very much!

# License

```
Copyright 2017 Jieyi Wu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
