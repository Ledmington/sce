use crate::node;

pub fn generalize<'a>(expr: &'a node::Node) -> node::Node<'a> {
    match *expr {
        node::Node::Bracket(Box::<node::Node>) => node::Node::Constant(*x),
        node::Node::Bracket(x) => node::Node::Bracket(Box::new(generalize(x))),
        node::Node::Plus(_, _) => todo!(),
        node::Node::Minus(_, _) => todo!(),
        node::Node::Multiply(_, _) => todo!(),
        node::Node::Divide(_, _) => todo!(),
        node::Node::Power(b, e) => {
            node::Node::Power(Box::new(generalize(b)), Box::new(generalize(e)))
        }
        node::Node::Constant(x) => node::Node::Constant(*x),
        node::Node::Variable(x) => node::Node::Variable(x),
        node::Node::Function(n, x) => node::Node::Function(n, Box::new(generalize(x))),
    }
}

pub fn simplify<'a>(expr: &'a node::Node) -> node::Node<'a> {
    match expr {
        node::Node::Bracket(_) => todo!(),
        node::Node::Plus(_, _) => todo!(),
        node::Node::Minus(_, _) => todo!(),
        node::Node::Multiply(_, _) => todo!(),
        node::Node::Divide(_, _) => todo!(),
        node::Node::Power(_, _) => todo!(),
        node::Node::Constant(_) => todo!(),
        node::Node::Variable(_) => todo!(),
        node::Node::Function(_, _) => todo!(),
    }
}
